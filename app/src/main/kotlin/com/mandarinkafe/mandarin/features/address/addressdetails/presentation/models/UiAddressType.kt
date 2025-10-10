package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.AddressType

enum class UiAddressType(
    @StringRes val nameRes: Int,
    @DrawableRes val iconRes: Int
) {
    APARTMENT(nameRes = R.string.address_type_apartment, iconRes = R.drawable.ic_apartment),
    PRIVATE_HOUSE(nameRes = R.string.address_type_private_house, iconRes = R.drawable.ic_cottage),
    OTHER(nameRes = R.string.address_type_other, iconRes = R.drawable.ic_nature_peple),
}

fun AddressType.toUi(): UiAddressType {
    return UiAddressType.valueOf(this.name)
}

fun UiAddressType.toDomain(): AddressType {
    return AddressType.valueOf(this.name)
}


