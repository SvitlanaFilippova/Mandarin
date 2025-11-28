package com.mandarinkafe.mandarin.features.order.presentation.models

import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource

enum class UiDeliveryType(
    val nameRes: StringResource,
    val iconRes: ImageResource,
) {
    DELIVERY(
        nameRes = MR.strings.delivery_type_name,
        iconRes = MR.images.ic_courier
    ),
    SELF_PICKUP(
        nameRes = MR.strings.selfpickup_type_name,
        iconRes = MR.images.ic_selfpickup
    )
}

fun DeliveryType.toUi(): UiDeliveryType {
    return UiDeliveryType.valueOf(this.name)
}

fun UiDeliveryType.toDomain(): DeliveryType {
    return DeliveryType.valueOf(this.name)
}