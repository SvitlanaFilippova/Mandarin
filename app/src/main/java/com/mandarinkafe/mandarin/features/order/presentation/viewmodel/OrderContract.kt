package com.mandarinkafe.mandarin.features.order.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.OrderPickupPoint
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.features.order.presentation.models.UiPaymentType
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.CartSummary
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.DeliveryInfo
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.PaymentInfo
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.UserInfo
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.Utensils
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

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
        val userInfo: UserInfo = UserInfo(),
        val deliveryInfo: DeliveryInfo = DeliveryInfo(),
        val paymentInfo: PaymentInfo = PaymentInfo(),
        val cartSummary: CartSummary = CartSummary(),
        val utensils: Utensils = Utensils(),
        val comment: String = "",
        val isError: Boolean = false,
        val pickupOnly: Boolean = false,
        val pickupPoint: OrderPickupPoint = OrderPickupPoint.CAFE
    ) : BaseState {
        val deliveryCost: Int
            get() = deliveryInfo.deliveryZone?.let { zone ->
                if (cartSummary.cartSumWithDiscount < zone.freeDeliveryThreshold) zone.deliveryPrice else 0
            } ?: 0

        val totalOrderSum: Double
            get() = cartSummary.cartSumWithDiscount + deliveryCost.toDouble()

        val canBeSubmitted: Boolean
            get() = userInfo.phoneIsValid &&
                    deliveryInfo.addressIsValid &&
                    paymentInfo.paymentTypeIsChosen

    }
}