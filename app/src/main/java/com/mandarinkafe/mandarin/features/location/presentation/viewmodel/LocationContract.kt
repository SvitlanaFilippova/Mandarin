package com.mandarinkafe.mandarin.features.location.presentation.viewmodel

import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState
import com.yandex.mapkit.geometry.Point

sealed interface LocationContract {

    sealed interface LocationEvent : BaseEvent {
        data object RequestLocation : LocationEvent
        data object GoBack : LocationEvent
        data class GoToTextSearch(val query: String) : LocationEvent
        data object GoToAddressDetails : LocationEvent
        data class CameraMoved(val center: Point) : LocationEvent
    }

    sealed interface LocationEffect : BaseEffect {
        data object GoBack : LocationEffect
        data class GoToAddressDetailsEffect(val address: UiAddress) : LocationEffect
        data class GoToTextSearchEffect(val query: String) : LocationEffect
    }

    data class LocationState(
        val isLoading: Boolean = false,
        val userLocation: Point? = null,
        val address: String? = null,
        val error: String? = null,
    ) : BaseState {
        val locationChosen: Boolean
            get() = address?.isNotEmpty() == true
    }
}