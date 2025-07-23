package com.mandarinkafe.mandarin.features.location.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.location.domain.api.GetAddressByPointUseCase
import com.mandarinkafe.mandarin.features.location.domain.api.GetCurrentLocationUseCase
import com.mandarinkafe.mandarin.features.location.domain.models.toGeoPoint
import com.mandarinkafe.mandarin.features.location.domain.models.toYandexPoint
import com.mandarinkafe.mandarin.features.location.presentation.viewmodel.LocationContract.LocationEffect
import com.mandarinkafe.mandarin.features.location.presentation.viewmodel.LocationContract.LocationEffect.GoBack
import com.mandarinkafe.mandarin.features.location.presentation.viewmodel.LocationContract.LocationEffect.GoToAddressDetailsEffect
import com.mandarinkafe.mandarin.features.location.presentation.viewmodel.LocationContract.LocationEffect.GoToTextSearchEffect
import com.mandarinkafe.mandarin.features.location.presentation.viewmodel.LocationContract.LocationEvent
import com.mandarinkafe.mandarin.features.location.presentation.viewmodel.LocationContract.LocationState
import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val getAddressByPoint: GetAddressByPointUseCase,
    private val getUserLocation: GetCurrentLocationUseCase,
) : BaseViewModel<LocationEvent, LocationEffect, LocationState>() {
    private val fetchAddressDebounce = debounce<Point>(
        FETCH_ADDRESS_DELAY,
        viewModelScope,
        useLastParam = true
    ) { point ->
        fetchAddress(point)
    }

    override fun setInitialState() = LocationState()

    override fun onEvent(event: LocationEvent) {
        when (event) {
            is LocationEvent.RequestLocation -> requestLocation()
            is LocationEvent.CameraMoved -> fetchAddressWithDebounce(event.center)
            is LocationEvent.GoBack -> sendEffect(GoBack)
            is LocationEvent.GoToTextSearch -> sendEffect(GoToTextSearchEffect(event.query))
            is LocationEvent.GoToAddressDetails -> goToAddressDetail()
        }
    }

    private fun goToAddressDetail() {
        val point = state.value.userLocation
        val address = state.value.address ?: ""
        point?.let {
            val address = UiAddress(
                point = point,
                addressMain = address
            )
            sendEffect(GoToAddressDetailsEffect(address))
        }
    }

    private fun fetchAddressWithDebounce(point: Point) {
        setState { copy(isLoading = true, error = null) }
        Log.d("DEBUG LOCATION", "fetchAddressWithDebounce called")
        fetchAddressDebounce.cancel()
        fetchAddressDebounce.invoke(point)
    }

    private fun fetchAddress(point: Point) {
        Log.d("DEBUG LOCATION", "fetchAddress called")
        viewModelScope.launch {
            val address = getAddressByPoint(point.toGeoPoint())
            Log.d("DEBUG LOCATION", "Current user address is: $address")
            if (address != null) {
                setState { copy(address = address, isLoading = false) }
            } else {
                setState { copy(error = "Не удалось определить адрес", isLoading = false) }
            }
        }
    }

    private fun requestLocation() {
        Log.d("DEBUG LOCATION", "requestLocation called")
        viewModelScope.launch {
            when (val result = getUserLocation()) {
                is Resource.Success -> {
                    val point = result.data?.toYandexPoint()
                    setState { copy(userLocation = point) }
                    Log.d("DEBUG LOCATION", "Current user location is: $point")

                }

                else -> {
                    setState {
                        copy(error = "Не удалось определить местоположение")
                    }
                }

            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = true) }
    }

    private companion object {
        private const val FETCH_ADDRESS_DELAY = 2000L
    }
}