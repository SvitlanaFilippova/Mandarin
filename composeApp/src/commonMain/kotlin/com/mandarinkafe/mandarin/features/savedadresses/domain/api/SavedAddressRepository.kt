package com.mandarinkafe.mandarin.features.savedadresses.domain.api

import com.mandarinkafe.mandarin.core.domain.models.Address

interface SavedAddressRepository {
    suspend fun getSavedAddresses(): List<Address>
    suspend fun saveAddress(address: Address)
    suspend fun removeAddress(id: String)
    suspend fun sync()
}