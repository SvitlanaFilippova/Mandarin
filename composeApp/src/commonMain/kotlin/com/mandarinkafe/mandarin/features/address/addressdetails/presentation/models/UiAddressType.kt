package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.models

import com.mandarinkafe.mandarin.core.domain.models.AddressType

enum class UiAddressType(
    val nameRes: String,
    val iconRes: String
) {
    APARTMENT(
        nameRes = "Квартира",
        iconRes = "ic_apartment"
    ),
    PRIVATE_HOUSE(
        nameRes = "Частный дом",
        iconRes = "ic_cottage"
    ),
    OTHER(
        nameRes = "Другое",
        iconRes = "ic_nature_peple"
    ),
}

fun AddressType.toUi(): UiAddressType {
    return UiAddressType.valueOf(this.name)
}

fun UiAddressType.toDomain(): AddressType {
    return AddressType.valueOf(this.name)
}