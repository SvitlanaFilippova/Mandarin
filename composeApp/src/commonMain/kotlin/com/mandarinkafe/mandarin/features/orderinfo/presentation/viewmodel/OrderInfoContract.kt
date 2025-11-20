package com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.UiDeliveryStatus
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.toUi
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.presentation.BaseContract
import dev.icerock.moko.resources.StringResource

sealed interface OrderInfoContract {

    sealed interface OrderInfoEvent : BaseContract.BaseEvent {
        data class SetInitData(val id: String, val isOnlinePayment: Boolean = false) :
            OrderInfoEvent

        data object StopObservingStatus : OrderInfoEvent
        data object CancelOrder : OrderInfoEvent
        data object RepeatOrder : OrderInfoEvent
        data object RefreshNow : OrderInfoEvent
        data object StartPayment : OrderInfoEvent
        data object RetryPayment : OrderInfoEvent
        data object DeleteOrderFromHistory : OrderInfoEvent
    }

    sealed interface OrderInfoEffect : BaseContract.BaseEffect {
        data class RepeatOrder(val hasInvalidItems: Boolean) : OrderInfoEffect
        data class ShowError(val message: String) : OrderInfoEffect
        data object NavigateBack : OrderInfoEffect
    }

    data class OrderInfoState(
        val orderId: String? = null,
        val isLoading: Boolean = true,
        val incomingOrder: IncomingOrder? = null,
        val orderRepeatingInProgress: Boolean = false,
        val paymentStatus: PaymentStatus? = null,
        val isPaymentPaid: Boolean? = null,
        val isPaymentLoading: Boolean = false,
        val isPaymentProcessing: Boolean = false,
        val isPaymentPolling: Boolean = false,
        val paymentError: StringResource? = null,
        val isOnlinePaymentFromNav: Boolean = false, // Флаг из навигации, приоритетный
    ) : BaseContract.BaseState {

        val deliveryStatus: UiDeliveryStatus
            get() = incomingOrder?.status?.toUi() ?: UiDeliveryStatus.UNCONFIRMED

        val isOnlinePayment: Boolean
            get() {
                // Приоритет: сначала проверяем флаг из навигации (для только что созданных заказов)
                if (isOnlinePaymentFromNav) {
                    return true
                }

                // Затем проверяем paymentMethodCode из IncomingOrder
                val paymentCode = incomingOrder?.paymentMethodCode

                return paymentCode?.equals(
                    Constants.PAYMENT_ONLINE_CODE,
                    ignoreCase = true
                ) == true
            }

        val isPaymentInProgress: Boolean
            get() = isPaymentLoading || isPaymentProcessing || isPaymentPolling

        val canShowPaymentError: Boolean
            get() = !isPaymentInProgress

        val canShowPaymentButton: Boolean
            get() = isPaymentPaid != true && !isPaymentInProgress && paymentStatus != PaymentStatus.SUCCEEDED

        val paymentCanBeChanged: Boolean
            get() = incomingOrder?.status == DeliveryStatus.UNCONFIRMED
                    || incomingOrder?.status == DeliveryStatus.WAIT_COOKING
                    || incomingOrder?.status == DeliveryStatus.READY_FOR_COOKING
                    || incomingOrder?.status == DeliveryStatus.COOKING_STARTED
    }
}

