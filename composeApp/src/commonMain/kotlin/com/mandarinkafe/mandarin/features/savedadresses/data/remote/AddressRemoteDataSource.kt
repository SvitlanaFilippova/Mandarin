package com.mandarinkafe.mandarin.features.savedadresses.data.remote

import com.mandarinkafe.mandarin.core.domain.models.Address

interface AddressRemoteDataSource {
    suspend fun getAddresses(): List<Address>
    suspend fun saveAddress(address: Address)
    suspend fun removeAddress(id: String)
}

