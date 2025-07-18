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
    }

    sealed interface OrderEffect : BaseEffect

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
    ) : BaseState
}