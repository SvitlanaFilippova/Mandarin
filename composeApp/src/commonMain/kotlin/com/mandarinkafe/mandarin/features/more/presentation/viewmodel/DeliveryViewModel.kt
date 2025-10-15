package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.address.data.Mapper.toGeoPoint
import com.mandarinkafe.mandarin.features.address.domain.api.AddressSearchInteractor
import com.mandarinkafe.mandarin.features.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.toUi
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DeliveryContract.DeliveryEffect
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DeliveryContract.DeliveryEvent
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DeliveryContract.DeliveryState
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.presentation.isSameAs
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeliveryViewModel(
    private val deliveryAreaRepository: DeliveryAreaRepository,
    private val searchInteractor: AddressSearchInteractor,
    private val getDeliveryZone: GetDeliveryZoneUseCase,
) :
    BaseViewModel<DeliveryEvent, DeliveryEffect, DeliveryState>() {
    override fun setInitialState() = DeliveryState()
    private val fetchAddressDebounce = debounce<Any>(
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

    override fun onEvent(event: DeliveryEvent) {
        when (event) {
            is DeliveryEvent.CameraMoved -> onCameraMoved(event.center)
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

    private fun onCameraMoved(point: Any) {
        val oldPoint = state.value.currentPinPoint
        if (oldPoint == null || !point.isSameAs(oldPoint)) {
            viewModelScope.launch {
                fetchAddressWithDebounce(point)
                checkDeliveryArea(point)
            }
        }
    }

    private fun fetchAddressWithDebounce(point: Any) {
        setState { copy(fetchAddressInProgress = true, error = null, currentPinPoint = point) }
        fetchAddressDebounce.cancel()
        fetchAddressDebounce.invoke(point)
    }

    private fun fetchAddress(point: Any) {
        viewModelScope.launch {
            searchInteractor.getAddressByPoint(point.toGeoPoint())
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
                                error = "Не удалось определить адрес", // TODO: Use MR.strings
                                fetchAddressInProgress = false,
                                displayAddress = null
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun checkDeliveryArea(point: Any) {
        val deliveryArea = getDeliveryZone(point.toGeoPoint())
        setState { copy(deliveryArea = deliveryArea?.toUi()) }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(fetchAddressInProgress = true, error = null, displayAddress = null) }
    }

    private companion object {
        private const val FETCH_ADDRESS_DELAY = 300L
    }
}
