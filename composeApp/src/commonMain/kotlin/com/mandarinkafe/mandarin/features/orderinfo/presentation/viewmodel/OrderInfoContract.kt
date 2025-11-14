package com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.UiDeliveryStatus
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.toUi
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.presentation.BaseContract
import dev.icerock.moko.resources.StringResource

sealed interface OrderInfoContract {

    sealed interface OrderInfoEvent : BaseContract.BaseEvent {
        data class SetInitId(val id: String) : OrderInfoEvent
        data object StopObservingStatus : OrderInfoEvent
        data object CancelOrder : OrderInfoEvent
        data object RepeatOrder : OrderInfoEvent
        data object RefreshNow : OrderInfoEvent
        data object StartPayment : OrderInfoEvent
        data object RetryPayment : OrderInfoEvent
    }

    sealed interface OrderInfoEffect : BaseContract.BaseEffect {
        data class RepeatOrder(val hasInvalidItems: Boolean) : OrderInfoEffect
        data class ShowError(val message: String) : OrderInfoEffect
    }

    data class OrderInfoState(
        val orderId: String? = null,
        val isLoading: Boolean = true,
        val incomingOrder: IncomingOrder? = null,
        val savedOrder: SavedOrder? = null,
        val orderRepeatingInProgress: Boolean = false,
        val paymentStatus: PaymentStatus? = null,
        val isPaymentPaid: Boolean? = null,
        val isPaymentLoading: Boolean = false,
        val isPaymentProcessing: Boolean = false,
        val isPaymentPolling: Boolean = false,
        val paymentError: StringResource? = null,
    ) : BaseContract.BaseState {

        val deliveryStatus: UiDeliveryStatus
            get() = incomingOrder?.status?.toUi() ?: UiDeliveryStatus.UNCONFIRMED
        
        val isOnlinePayment: Boolean
            get() {
                // Сначала проверяем paymentMethodCode из SavedOrder (если есть)
                val paymentCode = savedOrder?.paymentMethodCode
                    ?: incomingOrder?.paymentName // Fallback на paymentName из iiko
                
                return paymentCode?.equals(
                    Constants.PAYMENT_ONLINE_CODE,
                    ignoreCase = true
                ) == true
            }
    }
}

