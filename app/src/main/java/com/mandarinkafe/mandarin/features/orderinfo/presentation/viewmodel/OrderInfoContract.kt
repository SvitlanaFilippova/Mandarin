package com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.toUi
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.UiDeliveryStatus
import com.mandarinkafe.mandarin.util.BaseEffect
import com.mandarinkafe.mandarin.util.BaseEvent
import com.mandarinkafe.mandarin.util.BaseState

sealed interface OrderInfoContract {

    sealed interface OrderInfoEvent : BaseEvent {
        data class SetInitId(val id: String) : OrderInfoEvent
        data object StopObservingStatus : OrderInfoEvent
        data object CancelOrder : OrderInfoEvent
        data object RepeatOrder : OrderInfoEvent
        data object RefreshNow : OrderInfoEvent
    }

    sealed interface OrderInfoEffect : BaseEffect {
        data class ShowError(val message: String) : OrderInfoEffect
    }

    data class OrderInfoState(
        val orderId: String? = null,
        val isLoading: Boolean = false,
        val incomingOrder: IncomingOrder? = null
    ) : BaseState {

        val deliveryStatus: UiDeliveryStatus
            get() = incomingOrder?.status?.toUi() ?: UiDeliveryStatus.UNCONFIRMED
    }
}