package com.mandarinkafe.mandarin.features.payment.presentation.viewmodel

import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.util.presentation.BaseContract
import dev.icerock.moko.resources.StringResource

sealed interface PaymentContract {

    sealed interface PaymentEvent : BaseContract.BaseEvent {
        data object InitPayment : PaymentEvent
        data object RetryPayment : PaymentEvent
        data object CancelPayment : PaymentEvent
        data object DismissError : PaymentEvent
    }

    sealed interface PaymentEffect : BaseContract.BaseEffect {
        data class PaymentSuccess(val orderId: String) : PaymentEffect
        data class PaymentError(val message: StringResource) : PaymentEffect
        data class ShowCancelDialog(val orderId: String) : PaymentEffect
        data object PaymentCanceled : PaymentEffect
    }

    data class PaymentState(
        val orderId: String,
        val amount: Double,
        val isLoading: Boolean = false,
        val paymentStatus: PaymentStatus? = null,
        val error: StringResource? = null,
        val confirmationUrl: String? = null,
        val isPaymentProcessing: Boolean = false,  // Форма оплаты открыта
        val isPolling: Boolean = false,  // Опрашиваем статус
        val showCancelDialog: Boolean = false,
    ) : BaseContract.BaseState
}

