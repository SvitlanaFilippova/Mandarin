package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import com.mandarinkafe.mandarin.features.address.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.util.presentation.BaseEffect
import com.mandarinkafe.mandarin.util.presentation.BaseEvent
import com.mandarinkafe.mandarin.util.presentation.BaseState
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CENTER_LATITUDE
import com.mandarinkafe.mandarin.util.ConstantsMap.MANDARIN_CENTER_LONGITUDE
import com.yandex.mapkit.geometry.Point

sealed interface DeliveryContract {
    sealed interface DeliveryEvent : BaseEvent {
        data class CameraMoved(val center: Point) : DeliveryEvent
    }

    sealed interface DeliveryEffect : BaseEffect

    data class DeliveryState(
        val isLoading: Boolean = true,
        val initPinPoint: Point = Point(
            MANDARIN_CENTER_LATITUDE,
            MANDARIN_CENTER_LONGITUDE
        ),
        val deliveryAreas: List<UiDeliveryArea> = listOf(),
        val currentPinPoint: Point? = null,
        val displayAddress: String? = null,
        val deliveryArea: UiDeliveryArea? = null,
        val error: String? = null,
        val fetchAddressInProgress: Boolean = false,

        ) : BaseState {
        val locationChosen: Boolean
            get() = displayAddress?.isNotEmpty() == true && error == null && !fetchAddressInProgress
    }
}