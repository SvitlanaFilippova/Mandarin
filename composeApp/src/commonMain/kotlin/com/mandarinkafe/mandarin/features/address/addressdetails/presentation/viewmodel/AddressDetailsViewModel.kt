package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.AddressType
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailState
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEffect
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEffect.EditLocation
import com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel.AddressDetailsContract.AddressDetailsEvent
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.RemoveAddressUseCase
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SaveAddressUseCase
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import kotlinx.coroutines.launch

class AddressDetailsViewModel(
    private val saveAddressUseCase: SaveAddressUseCase,
    private val removeAddressUseCase: RemoveAddressUseCase,
) :
    BaseViewModel<AddressDetailsEvent, AddressDetailsEffect, AddressDetailState>() {
    override fun setInitialState() = AddressDetailState()

    override fun onEvent(event: AddressDetailsEvent) {
        when (event) {
            is AddressDetailsEvent.ChangeLocation -> changeLocation()
            is AddressDetailsEvent.SetInitAddress -> setAddress(event.address)
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
        viewModelScope.launch {
            saveAddressUseCase(state.value.address)
            sendEffect(AddressDetailsEffect.GoToParentScreen)
        }
    }

    private fun removeSavedAddress() {
        viewModelScope.launch {
            removeAddressUseCase(state.value.address.id)
            sendEffect(AddressDetailsEffect.GoToParentScreen)
        }
    }

    private fun setAddress(address: Address) {
        setState { copy(address = address) }
    }

    private fun setAddressType(type: AddressType) = updateAddress { copy(addressType = type) }

    private fun setApartmentNumber(query: String) = updateAddress {
        // Ограничиваем до 10 символов по требованию API
        val limited = query.take(MAX_ADDRESS_DETAILS_LENGTH)
        copy(apartmentNumber = limited)
    }

    private fun setEntrance(query: String) = updateAddress {
        // Ограничиваем до 10 символов по требованию API
        val limited = query.take(MAX_ADDRESS_DETAILS_LENGTH)
        copy(entrance = limited)
    }

    private fun setFloor(query: String) = updateAddress {
        // Ограничиваем до 10 символов по требованию API
        val limited = query.take(MAX_ADDRESS_DETAILS_LENGTH)
        copy(floor = limited)
    }

    private fun setIntercom(query: String) = updateAddress {
        // Ограничиваем до 10 символов по требованию API
        val limited = query.take(MAX_ADDRESS_DETAILS_LENGTH)
        copy(intercom = limited)
    }

    private fun setAddressComment(query: String) = updateAddress { copy(comment = query) }

    private fun updateAddress(transform: Address.() -> Address) {
        setState {
            copy(address = address.transform())
        }
    }

    private fun setError() {
        setState { copy(isError = true) }
    }

    private fun changeLocation() {
        sendEffect(EditLocation(state.value.address))
    }

    override fun setLoading(isLoading: Boolean) {
        // не применимо
    }

    private companion object {
        const val MAX_ADDRESS_DETAILS_LENGTH = 10
    }
}
