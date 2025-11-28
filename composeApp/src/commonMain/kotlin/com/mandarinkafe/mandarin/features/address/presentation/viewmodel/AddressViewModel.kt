package com.mandarinkafe.mandarin.features.address.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.domain.api.AddressSearchInteractor
import com.mandarinkafe.mandarin.features.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.address.domain.api.GetCurrentLocationUseCase
import com.mandarinkafe.mandarin.features.address.domain.api.GetDeliveryZoneUseCase
import com.mandarinkafe.mandarin.features.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.toUi
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract.AddressEffect
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract.AddressEffect.GoBack
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract.AddressEffect.GoToAddressDetailsEffect
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract.AddressEvent
import com.mandarinkafe.mandarin.features.address.presentation.viewmodel.AddressContract.AddressState
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.debounce
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import com.mandarinkafe.mandarin.util.presentation.createDefaultPoint
import com.mandarinkafe.mandarin.util.presentation.isSameAs
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddressViewModel(
    private val searchInteractor: AddressSearchInteractor,
    private val getUserLocation: GetCurrentLocationUseCase,
    private val getDeliveryZone: GetDeliveryZoneUseCase,
    private val deliveryAreaRepository: DeliveryAreaRepository,
) : BaseViewModel<AddressEvent, AddressEffect, AddressState>() {
    private val fetchAddressDebounce = debounce<GeoPoint>(
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
        setState { copy(initAddress = address, initPinPoint = address.point) }
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

    private fun onCameraMoved(point: GeoPoint) {
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
                                searchError = MR.strings.fail_to_fetch_address,
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
                searchInteractor.searchAddressByText(searchText, point)
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
                    initAddress.copy(point = point, streetAndBuilding = address)
                sendEffect(GoToAddressDetailsEffect(address))
            }

            point != null -> {
                val address = Address(
                    point = point,
                    streetAndBuilding = address
                )
                sendEffect(GoToAddressDetailsEffect(address))
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
                        if (address != null) {
                            setState {
                                copy(
                                    displayAddress = address.addressSingleLine.take(
                                        MAX_ADDRESS_LENGTH
                                    ),
                                    fetchAddressInProgress = false,
                                    error = null,
                                )
                            }
                        } else {
                            setState {
                                copy(
                                    error = "Не удалось определить адрес",
                                    fetchAddressInProgress = false,
                                    displayAddress = null
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
            val point = when (val result = getUserLocation()) {
                is Resource.Success -> {
                    result.data
                }

                else -> {
                    // В случае ошибки используем дефолтные координаты кафе
                    createDefaultPoint()
                }
            }
            setState { copy(userLocation = point) }
        }
    }

    private suspend fun checkDeliveryArea(point: GeoPoint) {
        val deliveryArea = getDeliveryZone(point)
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
            250 // вынуждены обрезать длинные адреса, чтобы iiko не вернул ошибку
    }
}


