package com.mandarinkafe.mandarin.features.savedadresses.data.impl

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.savedadresses.data.sharedprefs.AddressStorage
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SavedAddressRepository

class SavedAddressRepositoryImpl(private val storage: AddressStorage) : SavedAddressRepository {
    override suspend fun getSavedAddresses(): List<Address> {
        return storage.getSavedAddresses()
    }

    override suspend fun saveAddress(address: Address) {
        storage.saveAddress(address)
    }

    override suspend fun removeAddress(id: String) {
        storage.removeAddress(id)
    }
}