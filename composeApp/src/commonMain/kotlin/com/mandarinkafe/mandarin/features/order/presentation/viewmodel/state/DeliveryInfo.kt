package com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state

import androidx.compose.runtime.Stable
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.DeliveryZone
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType

@Stable
data class DeliveryInfo(
    val deliveryType: DeliveryType? = null,
    val savedAddresses: List<Address> = emptyList(),
    val chosenAddress: Address? = null,
    val deliveryZone: DeliveryZone? = null,
    val isLoading: Boolean = false,
) {
    val isDelivery: Boolean
        get() = deliveryType == DeliveryType.DELIVERY
    val isPickup: Boolean
        get() = deliveryType == DeliveryType.SELF_PICKUP

    val addressIsValid: Boolean
        get() = chosenAddress != null || isPickup

    val addressOutOfDeliveryZone: Boolean
        get() = isDelivery &&
                chosenAddress != null &&
                deliveryZone == null
}

