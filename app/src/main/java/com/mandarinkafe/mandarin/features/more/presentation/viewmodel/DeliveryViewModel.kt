package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.address.address.domain.api.AddressSearchInteractor
import com.mandarinkafe.mandarin.features.address.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.address.domain.models.toGeoPoint
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.models.toUi
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DeliveryContract.DeliveryEffect
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DeliveryContract.DeliveryEvent
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DeliveryContract.DeliveryState
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.isSameAs
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val deliveryAreaRepository: DeliveryAreaRepository,
    private val searchInteractor: AddressSearchInteractor,
    private val getDeliveryZone: GetDeliveryZoneUseCase,
) :
    BaseViewModel<DeliveryEvent, DeliveryEffect, DeliveryState>() {
    override fun setInitialState() = DeliveryState()
    private val fetchAddressDebounce = debounce<Point>(
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
                deliveryAreas?.let {
                    setState {
                        copy(
                            deliveryAreas = deliveryAreas,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun observeDisplayAddress() {
        viewModelScope.launch {
            searchInteractor.observeAddress().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        val address = result.data
                        address?.let {
                            setState {
                                copy(
                                    displayAddress = address.addressSingleLine.take(
                                        MAX_ADDRESS_LENGTH
                                    ),
                                    fetchAddressInProgress = false,
                                    error = null,
                                )
                            }
                        }
                    }

                    else -> {
                        setState {
                            copy(
                                error = "Не удалось определить адрес",
                                fetchAddressInProgress = false,
                                displayAddress = null
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onCameraMoved(point: Point) {
        val oldPoint = state.value.currentPinPoint
        if (oldPoint == null || !point.isSameAs(oldPoint)) {
            viewModelScope.launch {
                fetchAddressWithDebounce(point)
                checkDeliveryArea(point)
            }
        }
    }

    private fun fetchAddressWithDebounce(point: Point) {
        setState { copy(fetchAddressInProgress = true, error = null, currentPinPoint = point) }
        fetchAddressDebounce.cancel()
        fetchAddressDebounce.invoke(point)
    }

    private fun fetchAddress(point: Point) {
        viewModelScope.launch {
            searchInteractor.getAddressByPoint(point.toGeoPoint())
        }
    }

    private suspend fun checkDeliveryArea(point: Point) {
        val deliveryArea = getDeliveryZone(point.toGeoPoint())
        setState { copy(deliveryArea = deliveryArea?.toUi()) }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }

    private companion object {
        private const val FETCH_ADDRESS_DELAY = 1000L
        private const val MAX_ADDRESS_LENGTH = 250
    }
}