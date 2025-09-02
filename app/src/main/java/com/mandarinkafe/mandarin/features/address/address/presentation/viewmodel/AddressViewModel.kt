package com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.address.address.domain.api.AddressSearchInteractor
import com.mandarinkafe.mandarin.features.address.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetCurrentLocationUseCase
import com.mandarinkafe.mandarin.features.address.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.features.address.address.domain.models.toGeoPoint
import com.mandarinkafe.mandarin.features.address.address.domain.models.toYandexPoint
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.models.toUi
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressContract.AddressEffect
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressContract.AddressEffect.GoBack
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressContract.AddressEffect.GoToAddressDetailsEffect
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressContract.AddressEvent
import com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel.AddressContract.AddressState
import com.mandarinkafe.mandarin.util.Constants.MANDARIN_LATITUDE
import com.mandarinkafe.mandarin.util.Constants.MANDARIN_LONGITUDE
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
class AddressViewModel @Inject constructor(
    private val searchInteractor: AddressSearchInteractor,
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

    private val searchWithDebounce = debounce<String>(
        SEARCH_DELAY,
        viewModelScope,
        useLastParam = true
    ) {
        startSearch(it)
    }

    init {
        getDeliveryZones()
        observeSearchResults()
        observeDisplayAddress()
    }

    override fun setInitialState() = AddressState()

    override fun onEvent(event: AddressEvent) {
        when (event) {
            is AddressEvent.SetInitAddress -> setInitAddress(event.address)
            is AddressEvent.RequestAddress -> requestLocation()
            is AddressEvent.CameraMoved -> onCameraMoved(event.center)
            is AddressEvent.GoBack -> sendEffect(GoBack)
            is AddressEvent.ChangeSearchQuery -> changeSearchQuery(event.query)
            is AddressEvent.GoToAddressDetails -> goToAddressDetails()
        }
    }

    private fun setInitAddress(address: Address) {
        setState { copy(initAddress = address, initPinPoint = address.point?.toYandexPoint()) }
    }

    private fun getDeliveryZones() {
        viewModelScope.launch {
            val deliveryAreasResource = deliveryAreaRepository.getAllAreas()
            if (deliveryAreasResource is Resource.Success) {
                val deliveryAreas = deliveryAreasResource.data?.map { it.toUi() }
                deliveryAreas?.let { setState { copy(allDeliveryAreas = deliveryAreas) } }
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

    private fun observeSearchResults() {
        viewModelScope.launch {
            searchInteractor.observeSearchResults().collectLatest { result ->
                when (result) {
                    is Resource.Loading -> setSearchLoading()
                    is Resource.Success -> {
                        setSearchResult(result.data)
                    }

                    else -> {
                        setState {
                            copy(
                                searchError = "Не удалось найти адрес",
                                searchInProgress = false
                            )
                        }
                    }
                }
            }
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

    private fun startSearch(searchText: String) {
        searchWithDebounce.cancel()
        val point = state.value.currentPinPoint
        if (point == null) {
            return
        } else {
            viewModelScope.launch {
                searchInteractor.searchAddressByText(searchText, point.toGeoPoint())
            }
        }
    }

    private fun setSearchResult(data: List<AddressSearchResult>?) {
        data?.let {
            setState {
                copy(
                    searchError = null,
                    searchInProgress = false,
                    searchResults = data
                )
            }
        }
    }

    private fun goToAddressDetails() {
        val point = state.value.currentPinPoint
        val address = state.value.displayAddress ?: ""
        val initAddress = state.value.initAddress
        when {
            initAddress != null && point != null -> {
                val address =
                    initAddress.copy(point = point.toGeoPoint(), streetAndBuilding = address)
                sendEffect(GoToAddressDetailsEffect(address))
            }

            point != null -> {
                val address = Address(
                    point = point.toGeoPoint(),
                    streetAndBuilding = address
                )
                sendEffect(GoToAddressDetailsEffect(address))
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

    private fun requestLocation() {
        viewModelScope.launch {
            when (val result = getUserLocation()) {
                is Resource.Success -> {
                    val point = result.data?.toYandexPoint()
                    setState { copy(userLocation = point) }
                }

                else -> {
                    setState {
                        copy(
                            userLocation = Point(
                                MANDARIN_LATITUDE,
                                MANDARIN_LONGITUDE
                            )
                        )
                    }
                }
            }
        }
    }

    private suspend fun checkDeliveryArea(point: Point) {
        val deliveryArea = getDeliveryZone(point.toGeoPoint())
        setState { copy(currentDeliveryArea = deliveryArea?.toUi()) }
    }

    private fun setSearchLoading() {
        setState {
            copy(
                searchInProgress = true,
                searchError = null,
                searchResults = listOf()
            )
        }
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(fetchAddressInProgress = true, error = null, displayAddress = null) }
    }

    private companion object {
        private const val SEARCH_DELAY = 1000L
        private const val FETCH_ADDRESS_DELAY = 300L
        private const val MAX_ADDRESS_LENGTH =
            250 // вынуждены обрезать слишком длинные адреса, чтобы iiko при оформлении заказа не вернул ошибку
    }
}