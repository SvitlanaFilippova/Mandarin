package com.mandarinkafe.mandarin.features.savedadresses.data.datastore

import com.mandarinkafe.mandarin.core.domain.models.Address

interface AddressStorage {
    suspend fun getSavedAddresses(): List<Address>
    suspend fun saveAddress(item: Address)
    suspend fun removeAddress(id: String)
}





