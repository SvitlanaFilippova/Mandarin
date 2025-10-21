package com.mandarinkafe.mandarin.features.address.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.features.address.domain.models.AddressSearchResult
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.util.presentation.BaseContract
import dev.icerock.moko.resources.StringResource

sealed interface AddressContract {

    sealed interface AddressEvent : BaseContract.BaseEvent {
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
        data class CameraMoved(val center: GeoPoint) : AddressEvent
    }

    sealed interface AddressEffect : BaseContract.BaseEffect {
        data object GoBack : AddressEffect
        data class GoToAddressDetailsEffect(val address: Address) : AddressEffect
    }

    data class AddressState(
        val initAddress: Address? = null,
        val initPinPoint: GeoPoint? = null,
        val userLocation: GeoPoint? = null,
        val currentPinPoint: GeoPoint? = null,
        val displayAddress: String? = null,
        val currentDeliveryArea: UiDeliveryArea? = null,
        val allDeliveryAreas: List<UiDeliveryArea> = listOf(),
        val error: String? = null,
        val fetchAddressInProgress: Boolean = false,
        val searchInProgress: Boolean = false,
        val searchError: StringResource? = null,
        val searchResults: List<AddressSearchResult> = listOf()
    ) : BaseContract.BaseState {
        val locationChosen: Boolean
            get() = displayAddress?.isNotEmpty() == true && error == null && !searchInProgress && !fetchAddressInProgress
    }
}
