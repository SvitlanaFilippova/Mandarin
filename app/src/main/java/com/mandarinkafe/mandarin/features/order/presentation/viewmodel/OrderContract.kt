package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState
import com.mandarinkafe.mandarin.util.Constants.VALID_PHONE_LENGTH

sealed interface OrderContract {
    sealed interface OrderEvent : BaseEvent {
        data class SetName(val query: String) : OrderEvent
        data class SetPhone(val query: String) : OrderEvent
        data class SetDeliveryType(val deliveryType: DeliveryType) : OrderEvent
        data class SetAddress(val query: String) : OrderEvent
        data class SetAddressComment(val query: String) : OrderEvent
        data class SetApartmentNumber(val query: String) : OrderEvent
        data class SetEntrance(val query: String) : OrderEvent
        data class SetFloor(val query: String) : OrderEvent
        data class SetIntercom(val query: String) : OrderEvent
        data class SetPaymentType(val paymentType: PaymentType) : OrderEvent
        data class NoChangeToggled(val noChange: Boolean) : OrderEvent
        data class SetChangeFrom(val query: String) : OrderEvent
        data class SetNoNeedUtensils(val noNeedUtensils: Boolean) : OrderEvent
        data class SetChosenUtensils(val utensil: Utensil, val isChosen: Boolean) : OrderEvent
        data class SetComment(val query: String) : OrderEvent
        data object GetLocation : OrderEvent
        data object OnMissingRequiredInfo : OrderEvent
        data object SubmitOrder : OrderEvent
    }

    sealed interface OrderEffect : BaseEffect {
        data object SubmitOrder : OrderEffect
    }

    data class OrderState(
        val name: String = "",
        val phone: String = "",
        val deliveryType: DeliveryType? = null,
        val address: String = "",
        val addressComment: String = "",
        val apartmentNumber: String = "",
        val apartmentEntrance: String = "",
        val apartmentFloor: String = "",
        val apartmentIntercom: String = "",
        val paymentType: PaymentType? = null,
        val noChange: Boolean = false,
        val changeFrom: String = "",
        val noNeedUtensils: Boolean = false,
        val chosenUtensils: List<Utensil> = listOf(),
        val comment: String = "",
        val isError: Boolean = false,
        val discountPercent: Int = 0,
        val deliveryCost: Int = 0,
        val discountSum: Float = 0f,
    ) : BaseState {

        val phoneIsValid: Boolean
            get() = phone.length == VALID_PHONE_LENGTH
        val addressEntered: Boolean
            get() = address.isNotEmpty() || deliveryType == DeliveryType.SELF_PICKUP
        val apartmentDetailsIsValid: Boolean
            get() = deliveryType != DeliveryType.APARTMENT ||
                    apartmentNumber.isNotEmpty() && apartmentEntrance.isNotEmpty() && apartmentFloor.isNotEmpty()
        val paymentTypeChosen: Boolean
            get() = paymentType != null

        val canBeSubmitted: Boolean
            get() = phoneIsValid && addressEntered && apartmentDetailsIsValid && paymentTypeChosen
    }
}