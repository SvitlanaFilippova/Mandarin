package com.mandarinkafe.mandarin.features.savedadresses.data.sharedprefs

import com.mandarinkafe.mandarin.core.domain.models.Address

interface AddressStorage {
    fun getSavedAddresses(): List<Address>
    fun saveAddress(item: Address)
    fun removeAddress(id: String)
}

