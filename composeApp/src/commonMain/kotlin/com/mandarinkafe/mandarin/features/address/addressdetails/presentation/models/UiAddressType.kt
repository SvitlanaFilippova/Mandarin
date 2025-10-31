package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.models

import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.AddressType
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

enum class UiAddressType(
    val nameRes: StringResource,
    val iconRes: ImageResource,
) {
    APARTMENT(
        nameRes = MR.strings.address_type_apartment,
        iconRes = MR.images.ic_apartment
    ),
    PRIVATE_HOUSE(
        nameRes = MR.strings.address_type_private_house,
        iconRes = MR.images.ic_cottage
    ),
    OTHER(
        nameRes = MR.strings.address_type_other,
        iconRes = MR.images.ic_nature_peple
    ),
}

fun AddressType.toUi(): UiAddressType {
    return UiAddressType.valueOf(this.name)
}

fun UiAddressType.toDomain(): AddressType {
    return AddressType.valueOf(this.name)
}