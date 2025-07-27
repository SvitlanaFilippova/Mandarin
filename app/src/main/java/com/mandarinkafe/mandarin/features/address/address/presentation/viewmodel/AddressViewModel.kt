package com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.features.address.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetAddressByPointUseCase
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetCurrentLocationUseCase
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.address.domain.api.SearchAddressByTextUseCase
import com.mandarinkafe.mandarin.features.address.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.features.address.address.domain.models.toGeoPoint
import com.mandarinkafe.mandarin.features.address.address.domain.models.toYandexPoint
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressContract.AddressEffect
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressContract.AddressEffect.GoBack
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressContract.AddressEffect.GoToAddressDetailsEffect
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressContract.AddressEvent
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressContract.AddressState
import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val getAddressByPoint: GetAddressByPointUseCase,
    private val getUserLocation: GetCurrentLocationUseCase,
    private val getDeliveryZone: GetDeliveryZoneUseCase,
    private val searchAddressByText: SearchAddressByTextUseCase,
    private val deliveryAreaRepository: DeliveryAreaRepository
) : BaseViewModel<AddressEvent, AddressEffect, AddressState>() {
    private val logTag = "DEBUG MapKitFactory"
    private val fetchAddressDebounce = debounce<Point>(
        FETCH_ADDRESS_DELAY,
        viewModelScope,
        useLastParam = true
    ) { point ->
        fetchAddress(point)
    }

    private val searchWithDebounce = debounce<String>(
        SEARCH_DELAY,
        viewModelScope,
        useLastParam = true
    ) {
        startSearch(it)
    }

    init {
        setState { copy(deliveryAreas = deliveryAreaRepository.getAllAreas().map { it.toUi() }) }
        observeSearchResults()
        observeDisplayAddress()
    }

    override fun setInitialState() = AddressState()

    override fun onEvent(event: AddressEvent) {
        when (event) {
            is AddressEvent.RequestAddress -> requestLocation()
            is AddressEvent.CameraMoved -> onCameraMoved(event.center)
            is AddressEvent.GoBack -> sendEffect(GoBack)
            is AddressEvent.ChangeSearchQuery -> changeSearchQuery(event.query)
            is AddressEvent.GoToAddressDetails -> goToAddressDetails()
        }
    }

    private fun changeSearchQuery(query: String) {
        setState { copy(displayAddress = query) }
        fetchAddressDebounce.cancel()
        searchWithDebounce.cancel()
        if (query.isNotEmpty()) {
            searchWithDebounce.invoke(query)
            setSearchLoading()
        } else {
            setState { copy(searchError = null) }
        }

    }

    private fun onCameraMoved(point: Point) {
        fetchAddressWithDebounce(point)
        checkDeliveryArea(point)
    }

    private fun observeSearchResults() {
        viewModelScope.launch {
            searchAddressByText.observeSearchResults().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> setSearchLoading()
                    is Resource.Success -> {
                        setSearchResult(result.data)
                    }

                    else -> {
                        setState {
                            copy(
                                searchError = "Не удалось найти адрес",
                                searchIsLoading = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun startSearch(searchText: String) {
        searchWithDebounce.cancel()
        val point = state.value.currentPinPoint
        if (point == null) {
            Log.d(logTag, "Search aborted: empty point")
            return
        } else {
            setSearchLoading()

            viewModelScope.launch {
                searchAddressByText(searchText, point.toGeoPoint())
            }
        }
    }

    private fun setSearchResult(data: List<AddressSearchResult>?) {
        data?.let {
            setState {
                copy(
                    searchError = null,
                    searchIsLoading = false,
                    searchResults = data
                )
            }
        }
    }

    private fun goToAddressDetails() {
        val point = state.value.currentPinPoint
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
        setState { copy(isLoading = true, error = null, currentPinPoint = point) }
        fetchAddressDebounce.cancel()
        fetchAddressDebounce.invoke(point)
    }

    private fun fetchAddress(point: Point) {
        viewModelScope.launch {
            getAddressByPoint(point.toGeoPoint())
        }
    }

    private fun observeDisplayAddress() {
        viewModelScope.launch {
            getAddressByPoint.observeAddress().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> setLoading()
                    is Resource.Success -> {
                        val address = result.data
                        address?.let {
                            setState {
                                copy(
                                    displayAddress = address.addressSingleLine,
                                    isLoading = false,
                                    error = null,
                                )
                            }
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
                    setState { copy(initPinPoint = point) }
                }

                else -> {
                    setState {
                        copy(
                            initPinPoint = Point(
                                MANDARIN_LATITUDE,
                                MANDARIN_LONGITUDE
                            )
                        )
                    }
                }
            }
        }
    }

    private fun checkDeliveryArea(point: Point) {
        val deliveryArea = getDeliveryZone(point.toGeoPoint())
        setState { copy(deliveryArea = deliveryArea?.toUi()) }
    }

    private fun setSearchLoading() {
        setState {
            copy(
                searchIsLoading = true,
                searchError = null,
                searchResults = listOf()
            )
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = true, error = null) }
    }

    private companion object {
        private const val MANDARIN_LATITUDE = 55.998040
        private const val MANDARIN_LONGITUDE = 38.375328
        private const val SEARCH_DELAY = 2000L
        private const val FETCH_ADDRESS_DELAY = 1000L
    }
}