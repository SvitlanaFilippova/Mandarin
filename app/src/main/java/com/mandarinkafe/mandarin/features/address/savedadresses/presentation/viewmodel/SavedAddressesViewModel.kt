package com.mandarinkafe.mandarin.features.address.savedadresses.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.address.savedadresses.domain.api.GetSavedAddressesUseCase
import com.mandarinkafe.mandarin.features.address.savedadresses.domain.api.RemoveAddressUseCase
import com.mandarinkafe.mandarin.features.address.savedadresses.presentation.viewmodel.SavedAddressesContract.SavedAddressesEffect
import com.mandarinkafe.mandarin.features.address.savedadresses.presentation.viewmodel.SavedAddressesContract.SavedAddressesEvent
import com.mandarinkafe.mandarin.features.address.savedadresses.presentation.viewmodel.SavedAddressesContract.SavedAddressesState
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedAddressesViewModel @Inject constructor(
    private val getSavedAddressesUseCase: GetSavedAddressesUseCase,
    private val removeAddress: RemoveAddressUseCase,
) :
    BaseViewModel<SavedAddressesEvent, SavedAddressesEffect, SavedAddressesState>() {
    override fun setInitialState() = SavedAddressesState()

    init {
        getSavedAddresses()
    }

    override fun onEvent(event: SavedAddressesEvent) {
        when (event) {
            is SavedAddressesEvent.AddNewAddress -> createNewAddress()
            is SavedAddressesEvent.EditAddress -> goToAddressEdit(event.address)
            is SavedAddressesEvent.RemoveAddress -> removeSavedAddress(event.id)
            SavedAddressesEvent.RefreshAddresses -> getSavedAddresses()
        }
    }

    private fun getSavedAddresses() {
        viewModelScope.launch {
            val addressList = getSavedAddressesUseCase().reversed()
            setState {
                copy(data = addressList)
            }
        }
    }

    private fun createNewAddress() {
        sendEffect(SavedAddressesEffect.AddNewAddress)
    }

    private fun goToAddressEdit(address: Address) {
        sendEffect(SavedAddressesEffect.EditAddress(address))
    }

    private fun removeSavedAddress(id: String) {
        viewModelScope.launch { removeAddress(id) }
        getSavedAddresses()
    }

    override fun setLoading(isLoading: Boolean) {
        setState { copy(isLoading = isLoading) }
    }
}