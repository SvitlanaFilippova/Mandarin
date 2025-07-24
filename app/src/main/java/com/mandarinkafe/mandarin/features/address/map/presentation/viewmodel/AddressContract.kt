package com.mandarinkafe.mandarin.features.address.map.presentation.viewmodel

import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.VisibleRegion

sealed interface AddressContract {

    sealed interface AddressEvent : BaseEvent {
        data object RequestAddress : AddressEvent
        data object GoBack : AddressEvent
        data class SetVisibleRegion(val region: VisibleRegion?) : AddressEvent
        data class GoToTextSearch(val query: String) : AddressEvent
        data object GoToAddressDetails : AddressEvent
        data class CameraMoved(val center: Point) : AddressEvent
    }

    sealed interface AddressEffect : BaseEffect {
        data object GoBack : AddressEffect
        data class GoToAddressDetailsEffect(val address: UiAddress) : AddressEffect
        data class GoToTextSearchEffect(val query: String, val geometry: Geometry) : AddressEffect
    }

    data class AddressState(
        val isLoading: Boolean = false,
        val userLocation: Point? = null,
        val address: String? = null,
        val visibleRegion: VisibleRegion? = null,
        val error: String? = null,

        ) : BaseState {
        val locationChosen: Boolean
            get() = address?.isNotEmpty() == true
    }
}