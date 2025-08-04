package com.mandarinkafe.mandarin.features.orderconfirmation.presentation.viewmodel

import com.mandarinkafe.mandarin.features.order.domain.models.OrderInfo
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface OrderConfirmationContract {

    sealed interface OrderConfirmationEvent : BaseEvent {
        data class SetInitId(val id: String) : OrderConfirmationEvent
        data object StopObservingStatus : OrderConfirmationEvent
    }

    sealed interface OrderConfirmationEffect : BaseEffect {
        data class ShowError(val message: String) : OrderConfirmationEffect
    }

    data class OrderConfirmationState(
        val isLoading: Boolean = false,
        val orderInfo: OrderInfo? = null
    ) : BaseState
}