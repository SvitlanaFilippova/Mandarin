package com.mandarinkafe.mandarin.features.delivery.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.util.presentation.BaseContract
import com.mandarinkafe.mandarin.util.presentation.createDefaultPoint
import dev.icerock.moko.resources.StringResource

sealed interface DeliveryContract {
    sealed interface DeliveryEvent : BaseContract.BaseEvent {
        data class CameraMoved(val center: GeoPoint) : DeliveryEvent
    }

    sealed interface DeliveryEffect : BaseContract.BaseEffect

    data class DeliveryState(
        val isLoading: Boolean = true,
        val initPinPoint: GeoPoint = createDefaultPoint(),
        val deliveryAreas: List<UiDeliveryArea> = listOf(),
        val currentPinPoint: GeoPoint? = null,
        val displayAddress: String? = null,
        val deliveryArea: UiDeliveryArea? = null,
        val error: StringResource? = null,
        val fetchAddressInProgress: Boolean = false,

        ) : BaseContract.BaseState {
        val locationChosen: Boolean
            get() = displayAddress?.isNotEmpty() == true && error == null && !fetchAddressInProgress
    }
}



