package com.mandarinkafe.mandarin.features.savedadresses.data.impl

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.savedadresses.data.remote.AddressRemoteDataSource
import com.mandarinkafe.mandarin.features.savedadresses.domain.api.SavedAddressRepository

class SavedAddressRepositoryImpl(
    private val remoteDataSource: AddressRemoteDataSource,
) : SavedAddressRepository {
    override suspend fun getSavedAddresses(): List<Address> {
        return remoteDataSource.getAddresses()
    }

    override suspend fun saveAddress(address: Address) {
        remoteDataSource.saveAddress(address)
    }

    override suspend fun removeAddress(id: String) {
        remoteDataSource.removeAddress(id)
    }
}






