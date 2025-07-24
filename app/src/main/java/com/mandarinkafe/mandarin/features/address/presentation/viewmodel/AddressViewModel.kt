package com.mandarinkafe.mandarin.features.address.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.address.domain.api.GetAddressByPointUseCase
import com.mandarinkafe.mandarin.features.address.domain.api.GetCurrentLocationUseCase
import com.mandarinkafe.mandarin.features.address.domain.models.toGeoPoint
import com.mandarinkafe.mandarin.features.address.domain.models.toYandexPoint
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract.AddressEffect
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract.AddressEffect.GoBack
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract.AddressEffect.GoToAddressDetailsEffect
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract.AddressEffect.GoToTextSearchEffect
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract.AddressEvent
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract.AddressState
import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val getAddressByPoint: GetAddressByPointUseCase,
    private val getUserLocation: GetCurrentLocationUseCase,
) : BaseViewModel<AddressEvent, AddressEffect, AddressState>() {
    private val fetchAddressDebounce = debounce<Point>(
        FETCH_ADDRESS_DELAY,
        viewModelScope,
        useLastParam = true
    ) { point ->
        fetchAddress(point)
    }

    override fun setInitialState() = AddressState()

    override fun onEvent(event: AddressEvent) {
        when (event) {
            is AddressEvent.RequestAddress -> requestLocation()
            is AddressEvent.CameraMoved -> fetchAddressWithDebounce(event.center)
            is AddressEvent.GoBack -> sendEffect(GoBack)
            is AddressEvent.GoToTextSearch -> sendEffect(GoToTextSearchEffect(event.query))
            is AddressEvent.GoToAddressDetails -> goToAddressDetails()
        }
    }

    private fun goToAddressDetails() {
        val point = state.value.userLocation
        val address = state.value.address ?: ""
        point?.let {
            val address = UiAddress(
                point = point,
                streetAndBuilding = address
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
            getAddressByPoint(point.toGeoPoint())
            delay(1000)
            getAddressByPoint.observeAddress().collectLatest { result ->
                Log.d("DEBUG LOCATION", "Current result is: $result")
                if (result is Resource.Success) {
                    val address = result.data
                    Log.d("DEBUG LOCATION", "Current user address is: $address.")
                    setState { copy(address = address, isLoading = false) }
                } else {
                    setState { copy(error = "Не удалось определить адрес", isLoading = false) }
                }
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