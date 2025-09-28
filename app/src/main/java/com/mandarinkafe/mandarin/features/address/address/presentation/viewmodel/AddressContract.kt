package com.mandarinkafe.mandarin.features.address.address.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.address.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState
import com.yandex.mapkit.geometry.Point

sealed interface AddressContract {

    sealed interface AddressEvent : BaseEvent {
        // Установка начального состояния
        data class SetInitAddress(val address: Address) : AddressEvent

        // Запрос данных
        data object RequestAddress : AddressEvent

        // Навигация
        data object GoBack : AddressEvent
        data object GoToAddressDetails : AddressEvent

        // Поиск адреса
        data class ChangeSearchQuery(val query: String) : AddressEvent

        // Взаимодействие с картой
        data class CameraMoved(val center: Point) : AddressEvent
    }

    sealed interface AddressEffect : BaseEffect {
        data object GoBack : AddressEffect
        data class GoToAddressDetailsEffect(val address: Address) : AddressEffect
    }

    data class AddressState(
        val initAddress: Address? = null,
        val initPinPoint: Point? = null,
        val userLocation: Point? = null,
        val currentPinPoint: Point? = null,
        val displayAddress: String? = null,
        val currentDeliveryArea: UiDeliveryArea? = null,
        val allDeliveryAreas: List<UiDeliveryArea> = listOf(),
        val error: String? = null,
        val fetchAddressInProgress: Boolean = false,
        val searchInProgress: Boolean = false,
        val searchError: String? = null,
        val searchResults: List<AddressSearchResult> = listOf()
    ) : BaseState {
        val locationChosen: Boolean
            get() = displayAddress?.isNotEmpty() == true && error == null && !searchInProgress && !fetchAddressInProgress
    }
}