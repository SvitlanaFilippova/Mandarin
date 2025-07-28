package com.mandarinkafe.mandarin.core.domain.models

import com.mandarinkafe.mandarin.R

enum class AddressType(val nameRes: Int) {
    APARTMENT(R.string.address_type_apartment),
    PRIVATE_HOUSE(R.string.address_type_private_house),
    OTHER(R.string.address_type_other),
}