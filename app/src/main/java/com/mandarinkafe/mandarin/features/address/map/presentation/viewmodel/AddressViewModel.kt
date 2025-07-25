package com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.address.map.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.map.domain.api.GetAddressByPointUseCase
import com.mandarinkafe.mandarin.features.address.map.domain.api.GetCurrentLocationUseCase
import com.mandarinkafe.mandarin.features.address.map.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.map.domain.models.toGeoPoint
import com.mandarinkafe.mandarin.features.address.map.domain.models.toYandexPoint
import com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel.AddressContract.AddressEffect
import com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel.AddressContract.AddressEffect.GoBack
import com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel.AddressContract.AddressEffect.GoToAddressDetailsEffect
import com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel.AddressContract.AddressEffect.GoToTextSearchEffect
import com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel.AddressContract.AddressEvent
import com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel.AddressContract.AddressState
import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.VisibleRegion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val getAddressByPoint: GetAddressByPointUseCase,
    private val getUserLocation: GetCurrentLocationUseCase,
    private val getDeliveryZone: GetDeliveryZoneUseCase,
    private val deliveryAreaRepository: DeliveryAreaRepository
) : BaseViewModel<AddressEvent, AddressEffect, AddressState>() {
    private val fetchAddressDebounce = debounce<Point>(
        FETCH_ADDRESS_DELAY,
        viewModelScope,
        useLastParam = true
    ) { point ->
        fetchAddress(point)
    }

    init {
        setState { copy(deliveryAreas = deliveryAreaRepository.getAllAreas().map { it.toUi() }) }
    }

    override fun setInitialState() = AddressState()

    override fun onEvent(event: AddressEvent) {
        when (event) {
            is AddressEvent.RequestAddress -> requestLocation()
            is AddressEvent.CameraMoved -> fetchAddressWithDebounce(event.center)
            is AddressEvent.GoBack -> sendEffect(GoBack)
            is AddressEvent.GoToTextSearch -> goToTextSearch()
            is AddressEvent.GoToAddressDetails -> goToAddressDetails()
            is AddressEvent.SetVisibleRegion -> setVisibleRegion(event.region)
        }
    }

    private fun goToTextSearch() {
        val visibleRegion = state.value.visibleRegion ?: return
        val geometry = visibleRegionToBoundingBox(visibleRegion)

        sendEffect(
            GoToTextSearchEffect(
                query = state.value.displayAddress.orEmpty(),
                geometry = geometry
            )
        )
    }

    private fun setVisibleRegion(region: VisibleRegion?) {
        setState { copy(visibleRegion = region) }
    }

    private fun goToAddressDetails() {
        val point = state.value.initPinLocation
        val address = state.value.displayAddress ?: ""
        point?.let {
            val address = UiAddress(
                point = point.toGeoPoint(),
                streetAndBuilding = address
            )
            sendEffect(GoToAddressDetailsEffect(address))
        }
    }

    private fun fetchAddressWithDebounce(point: Point) {
        setLoading()
        fetchAddressDebounce.cancel()
        fetchAddressDebounce.invoke(point)
    }

    private fun fetchAddress(point: Point) {
        viewModelScope.launch {
            getAddressByPoint(point.toGeoPoint())
            getAddressByPoint.observeAddress().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> setLoading()
                    is Resource.Success -> {
                        val address = result.data
                        val deliveryArea = getDeliveryZone(point.toGeoPoint())
                        setState {
                            copy(
                                displayAddress = address,
                                isLoading = false,
                                error = null,
                                deliveryArea = deliveryArea?.toUi()
                            )
                        }
                    }

                    else -> {
                        setState {
                            copy(
                                error = "Не удалось определить адрес",
                                isLoading = false,
                                displayAddress = null
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestLocation() {
        viewModelScope.launch {
            when (val result = getUserLocation()) {
                is Resource.Success -> {
                    val point = result.data?.toYandexPoint()
                    setState { copy(initPinLocation = point) }
                }

                else -> {
                    setState {
                        copy(
                            initPinLocation = Point(
                                MANDARIN_LATITUDE,
                                MANDARIN_LONGITUDE
                            )
                        )
                    }
                }
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = true, error = null) }
    }

    private companion object {
        private const val MANDARIN_LATITUDE = 55.998040
        private const val MANDARIN_LONGITUDE = 38.375328
        private const val FETCH_ADDRESS_DELAY = 1000L
    }
}