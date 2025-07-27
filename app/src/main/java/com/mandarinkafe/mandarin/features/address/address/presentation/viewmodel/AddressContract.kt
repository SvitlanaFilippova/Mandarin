package com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel

import com.mandarinkafe.mandarin.features.address.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState
import com.yandex.mapkit.geometry.Point

sealed interface AddressContract {

    sealed interface AddressEvent : BaseEvent {
        data object RequestAddress : AddressEvent
        data object GoBack : AddressEvent
        data class ChangeSearchQuery(val query: String) : AddressEvent
        data object GoToAddressDetails : AddressEvent
        data class CameraMoved(val center: Point) : AddressEvent
    }

    sealed interface AddressEffect : BaseEffect {
        data object GoBack : AddressEffect
        data class GoToAddressDetailsEffect(val address: UiAddress) : AddressEffect
    }

    data class AddressState(
        val isLoading: Boolean = false,
        val initPinPoint: Point? = null,
        val currentPinPoint: Point? = null,
        val displayAddress: String? = null,
        val deliveryArea: UiDeliveryArea? = null,
        val deliveryAreas: List<UiDeliveryArea> = listOf(),
        val error: String? = null,
        val searchIsLoading: Boolean = false,
        val searchError: String? = null,
        val searchResults: List<AddressSearchResult> = listOf()
    ) : BaseState {
        val locationChosen: Boolean
            get() = displayAddress?.isNotEmpty() == true && error == null
    }
}