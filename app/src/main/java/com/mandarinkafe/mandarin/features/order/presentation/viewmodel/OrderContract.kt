package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface OrderContract {
    sealed interface OrderEvent : BaseEvent {
        data class SetName(val query: String) : OrderEvent
        data class SetPhone(val query: String) : OrderEvent
        data class SetDeliveryType(val deliveryType: DeliveryType) : OrderEvent
        data class SetAddress(val query: String) : OrderEvent
        data class SetApartmentDetails(val query: String) : OrderEvent
        data class SetPaymentType(val paymentType: PaymentType) : OrderEvent
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
        val apartmentDetails: String = "",
        val paymentType: PaymentType? = null,
        val changeFrom: String = "",
        val noNeedUtensils: Boolean = false,
        val chosenUtensils: List<Utensil> = listOf(),
        val comment: String = "",
        val isError: Boolean = false,
        val discountPercent: Int = 10,
        val deliveryCost: Int = 0,
        val discountSum: Float = 0f,
    ) : BaseState {

        val phoneIsValid: Boolean
            get() = phone.isNotEmpty()
        val addressEntered: Boolean
            get() = address.isNotEmpty() || deliveryType == DeliveryType.SELF_PICKUP
        val apartmentDetailsIsValid: Boolean
            get() = apartmentDetails.isNotEmpty() || deliveryType != DeliveryType.APARTMENT
        val paymentTypeChosen: Boolean
            get() = paymentType != null

        val canBeSubmitted: Boolean
            get() = phoneIsValid && addressEntered && apartmentDetailsIsValid && paymentTypeChosen
    }
}