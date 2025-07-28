package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.AddressType
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailState
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEffect
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEffect.EditLocation
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEvent
import com.mandarinkafe.mandarin.features.address.savedadresses.domain.api.RemoveAddressUseCase
import com.mandarinkafe.mandarin.features.address.savedadresses.domain.api.SaveAddressUseCase
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressDetailsViewModel @Inject constructor(
    private val saveAddressUseCase: SaveAddressUseCase,
    private val removeAddressUseCase: RemoveAddressUseCase
) :
    BaseViewModel<AddressDetailsEvent, AddressDetailsEffect, AddressDetailState>() {
    override fun setInitialState() = AddressDetailState()

    override fun onEvent(event: AddressDetailsEvent) {
        when (event) {
            is AddressDetailsEvent.ChangeLocation -> changeLocation()
            is AddressDetailsEvent.SetAddress -> setAddress(event.address)
            is AddressDetailsEvent.SetApartmentNumber -> setApartmentNumber(event.query)
            is AddressDetailsEvent.SetEntrance -> setEntrance(event.query)
            is AddressDetailsEvent.SetFloor -> setFloor(event.query)
            is AddressDetailsEvent.SetIntercom -> setIntercom(event.query)
            is AddressDetailsEvent.SetAddressComment -> setAddressComment(event.query)
            is AddressDetailsEvent.OnMissingRequiredInfo -> setError()
            is AddressDetailsEvent.SaveAddress -> saveAddress()
            is AddressDetailsEvent.RemoveAddress -> removeSavedAddress()
            is AddressDetailsEvent.SetAddressType -> setAddressType(event.addressType)
        }
    }

    private fun saveAddress() {
        viewModelScope.launch { saveAddressUseCase(state.value.address) }
    }

    private fun removeSavedAddress() {
        viewModelScope.launch { removeAddressUseCase(state.value.address.id) }
    }

    private fun setError() {
        setState { copy(isError = true) }
    }

    private fun setAddress(address: Address) {
        setState {
            copy(address = address)
        }
    }

    private fun changeLocation() {
        val currentAddress = state.value.address
        sendEffect(
            EditLocation(
                address = currentAddress
            )
        )
    }

    private fun setAddressType(type: AddressType) = updateAddress { copy(addressType = type) }

    private fun setApartmentNumber(query: String) = updateAddress { copy(apartmentNumber = query) }

    private fun setEntrance(query: String) = updateAddress { copy(entrance = query) }

    private fun setFloor(query: String) = updateAddress { copy(floor = query) }

    private fun setIntercom(query: String) = updateAddress { copy(intercom = query) }

    private fun setAddressComment(query: String) = updateAddress { copy(comment = query) }

    private fun updateAddress(transform: Address.() -> Address) {
        setState {
            copy(address = address.transform())

        }
    }

    override fun setLoading(isLoading: Boolean) {
        // не применимо
    }
}