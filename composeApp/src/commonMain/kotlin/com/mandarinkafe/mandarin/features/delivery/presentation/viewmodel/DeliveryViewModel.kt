package com.mandarinkafe.mandarin.features.delivery.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.domain.api.AddressSearchInteractor
import com.mandarinkafe.mandarin.features.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.domain.api.GetCurrentLocationUseCase
import com.mandarinkafe.mandarin.features.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.toUi
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.mandarinkafe.mandarin.util.presentation.isSameAs
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeliveryViewModel(
    private val deliveryAreaRepository: DeliveryAreaRepository,
    private val searchInteractor: AddressSearchInteractor,
    private val getDeliveryZone: GetDeliveryZoneUseCase,
    private val getUserLocation: GetCurrentLocationUseCase,
) :
    BaseViewModel<DeliveryContract.DeliveryEvent, DeliveryContract.DeliveryEffect, DeliveryContract.DeliveryState>() {
    override fun setInitialState() = DeliveryContract.DeliveryState()
    private val fetchAddressDebounce = debounce<GeoPoint>(
        FETCH_ADDRESS_DELAY,
        viewModelScope,
        useLastParam = true
    ) { point ->
        fetchAddress(point)
    }

    init {
        getDeliveryZones()
        observeDisplayAddress()
    }

    override fun onEvent(event: DeliveryContract.DeliveryEvent) {
        when (event) {
            is DeliveryContract.DeliveryEvent.CameraMoved -> onCameraMoved(event.center)
            DeliveryContract.DeliveryEvent.RequestAddress -> requestLocation()
        }
    }

    private fun requestLocation() {
        viewModelScope.launch {
            val point = when (val result = getUserLocation()) {
                is Resource.Success -> {
                    result.data
                }

                else -> {
                    null
                }
            }
            setState { copy(userLocation = point) }
        }
    }

    private fun getDeliveryZones() {
        viewModelScope.launch {
            val deliveryAreasResource = deliveryAreaRepository.getAllAreas()
            if (deliveryAreasResource is Resource.Success) {
                val deliveryAreas = deliveryAreasResource.data?.map { it.toUi() }
                deliveryAreas?.let { setState { copy(deliveryAreas = deliveryAreas) } }
            }
        }
    }

    private fun onCameraMoved(point: GeoPoint) {
        val oldPoint = state.value.currentPinPoint
        if (oldPoint == null || !point.isSameAs(oldPoint)) {
            viewModelScope.launch {
                fetchAddressWithDebounce(point)
                checkDeliveryArea(point)
            }
        }
    }

    private fun fetchAddressWithDebounce(point: GeoPoint) {
        setState { copy(fetchAddressInProgress = true, error = null, currentPinPoint = point) }
        fetchAddressDebounce.cancel()
        fetchAddressDebounce.invoke(point)
    }

    private fun fetchAddress(point: GeoPoint) {
        viewModelScope.launch {
            searchInteractor.getAddressByPoint(point)
        }
    }

    private fun observeDisplayAddress() {
        viewModelScope.launch {
            searchInteractor.observeAddress().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> setLoading()
                    is Resource.Success -> {
                        val address = result.data
                        address?.let {
                            setState {
                                copy(
                                    displayAddress = address.addressSingleLine,
                                    fetchAddressInProgress = false,
                                    error = null,
                                )
                            }
                        }
                    }

                    else -> {
                        setState {
                            copy(
                                error = MR.strings.fail_to_fetch_address,
                                fetchAddressInProgress = false,
                                displayAddress = null
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun checkDeliveryArea(point: GeoPoint) {
        val deliveryArea = getDeliveryZone(point)
        setState { copy(deliveryArea = deliveryArea?.toUi()) }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(fetchAddressInProgress = true, error = null, displayAddress = null) }
    }

    private companion object {
        private const val FETCH_ADDRESS_DELAY = 300L
    }
}

