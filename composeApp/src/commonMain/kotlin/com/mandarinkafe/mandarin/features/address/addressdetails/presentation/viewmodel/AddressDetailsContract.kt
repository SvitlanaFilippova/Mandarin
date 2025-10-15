package com.mandarinkafe.mandarin.features.address.addressdetails.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.AddressType
import com.mandarinkafe.mandarin.util.presentation.BaseContract

sealed interface AddressDetailsContract {
    sealed interface AddressDetailsEvent : BaseContract.BaseEvent {
        // Установка адреса при инициализации
        data class SetInitAddress(val address: Address) : AddressDetailsEvent

        // Установка типа адреса
        data class SetAddressType(val addressType: AddressType) : AddressDetailsEvent

        // Ввод деталей адреса
        data class SetApartmentNumber(val query: String) : AddressDetailsEvent
        data class SetEntrance(val query: String) : AddressDetailsEvent
        data class SetFloor(val query: String) : AddressDetailsEvent
        data class SetIntercom(val query: String) : AddressDetailsEvent
        data class SetAddressComment(val query: String) : AddressDetailsEvent

        // Действия с адресом
        data object SaveAddress : AddressDetailsEvent
        data object RemoveAddress : AddressDetailsEvent

        // Обработка ошибок
        data object OnMissingRequiredInfo : AddressDetailsEvent

        // Попытка изменить изначальный адрес -> переход на карту
        data object ChangeLocation : AddressDetailsEvent
    }

    sealed interface AddressDetailsEffect : BaseContract.BaseEffect {
        data class EditLocation(val address: Address) : AddressDetailsEffect
        data object GoToParentScreen : AddressDetailsEffect
        data object ShowDeleteConfirmDialog : AddressDetailsEffect
    }

    data class AddressDetailState(
        val address: Address = Address(),
        val isError: Boolean = false,
    ) : BaseContract.BaseState {

        val addressIsValid: Boolean
            get() =
                with(address) {
                    noNeedAddressDetails ||
                            apartmentNumber.isNotEmpty() && entrance.isNotEmpty() && floor.isNotEmpty()
                }
    }
}
