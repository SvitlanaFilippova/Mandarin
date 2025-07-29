package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.OrderPickupPoint
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.features.order.presentation.models.UiPaymentType
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState
import com.mandarinkafe.mandarin.util.Constants.VALID_PHONE_LENGTH

sealed interface OrderContract {
    sealed interface OrderEvent : BaseEvent {
        data class SetName(val query: String) : OrderEvent
        data class SetPhone(val query: String) : OrderEvent
        data object RefreshAddresses : OrderEvent
        data class SetDeliveryType(val deliveryType: DeliveryType) : OrderEvent
        data class SetAddress(val address: Address) : OrderEvent
        data object AddNewAddress : OrderEvent
        data class EditAddress(val address: Address) : OrderEvent
        data class RemoveAddress(val id: String) : OrderEvent
        data class SetPaymentType(val paymentType: UiPaymentType) : OrderEvent
        data class NoChangeToggled(val noChange: Boolean) : OrderEvent
        data class SetChangeFrom(val query: String) : OrderEvent
        data class SetNoNeedUtensils(val noNeedUtensils: Boolean) : OrderEvent
        data class SetChosenUtensils(val utensil: Utensil, val isChosen: Boolean) : OrderEvent
        data class SetComment(val query: String) : OrderEvent
        data class SelectAddressById(val id: String) : OrderEvent
        data object OnMissingRequiredInfo : OrderEvent
        data object SubmitOrder : OrderEvent
        data object GetPaymentTypes : OrderEvent
    }

    sealed interface OrderEffect : BaseEffect {
        data object AddNewAddress : OrderEffect
        data class EditAddress(val address: Address) : OrderEffect
        data object SubmitOrder : OrderEffect
    }

    data class OrderState(
        val name: String = "",
        val phone: String = "",
        val deliveryType: DeliveryType? = null,
        val chosenAddress: Address? = null,
        val savedAddresses: List<Address> = listOf(),
        val availablePaymentTypes: List<PaymentType> = emptyList(),
        val chosenPaymentType: UiPaymentType? = null,
        val noChange: Boolean = false,
        val changeFrom: String = "",
        val noNeedUtensils: Boolean = false,
        val chosenUtensils: List<Utensil> = listOf(),
        val comment: String = "",
        val isError: Boolean = false,
        val cartItems: Map<CustomizedMeal, Int> = emptyMap(),
        val containNotDiscountable: Boolean = false,
        val totalCartSum: Int? = null,
        val deliveryFreeThreshold: Int? = null,
        val deliveryBasePrice: Int? = null,
        val deliveryRealCost: Int? = null,
        val discountSize: Int = 0,
        val discountSum: Double = 0.0,
        val totalCartSumWithDiscount: Double = 0.0,
        val totalOrderSum: Double = 0.0,
        val pickupOnly: Boolean = false,
        val pickupPoint: OrderPickupPoint = OrderPickupPoint.CAFE

    ) : BaseState {
        val phoneIsValid: Boolean
            get() = phone.length == VALID_PHONE_LENGTH
        val addressEntered: Boolean
            get() =
                chosenAddress != null || deliveryType == DeliveryType.SELF_PICKUP
        val addressInNotInDeliveryArea: Boolean
            get() = deliveryType == DeliveryType.DELIVERY && chosenAddress != null && deliveryFreeThreshold == null
        val paymentTypeIsChosen: Boolean
            get() = chosenPaymentType != null
        val canBeSubmitted: Boolean
            get() = phoneIsValid && addressEntered && paymentTypeIsChosen

    }
}