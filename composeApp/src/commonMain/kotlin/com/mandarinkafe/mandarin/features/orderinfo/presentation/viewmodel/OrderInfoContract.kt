package com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderinfo.domain.getPaymentMethodCode
import com.mandarinkafe.mandarin.features.orderinfo.domain.isOnlinePayment
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.UiDeliveryStatus
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.toUi
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.util.presentation.BaseContract
import dev.icerock.moko.resources.StringResource

sealed interface OrderInfoContract {

    sealed interface OrderInfoEvent : BaseContract.BaseEvent {
        data class SetInitData(val id: String, val paymentMethodCode: String? = null) :
            OrderInfoEvent

        data object StopObservingStatus : OrderInfoEvent
        data object CancelOrder : OrderInfoEvent
        data object RepeatOrder : OrderInfoEvent
        data object RefreshNow : OrderInfoEvent
        data object StartPayment : OrderInfoEvent
        data object RetryPayment : OrderInfoEvent
        data object DeleteOrderFromHistory : OrderInfoEvent
        data object LoadPaymentTypesForChange :
            OrderInfoEvent // Загрузить доступные способы оплаты для диалога

        data class ChangePaymentMethod(val paymentMethodCode: String) : OrderInfoEvent
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
        val paymentMethodCodeFromNav: String? = null, // Код способа оплаты из навигации, используется если order.paymentMethodCode == null
        val isChangingPaymentMethod: Boolean = false, // Индикатор загрузки при изменении способа оплаты
        // Доступные способы оплаты для диалога (только CASH, BANK, ONLINE)
        val availablePaymentTypes: List<com.mandarinkafe.mandarin.features.order.domain.models.PaymentType> = emptyList(),
        val paymentTimeRemainingSeconds: Int? = null, // Оставшееся время на оплату в секундах
        val isAutoCanceling: Boolean = false, // Флаг автоматической отмены заказа при истечении таймера
    ) : BaseContract.BaseState {

        val deliveryStatus: UiDeliveryStatus
            get() = incomingOrder?.status?.toUi(incomingOrder.isDelivery)
                ?: UiDeliveryStatus.UNCONFIRMED

        val isOnlinePayment: Boolean
            get() = incomingOrder.isOnlinePayment(paymentMethodCodeFromNav)

        val displayPaymentMethodCode: String?
            get() = incomingOrder.getPaymentMethodCode(paymentMethodCodeFromNav)

        val isPaymentInProgress: Boolean
            get() = isPaymentLoading || isPaymentProcessing || isPaymentPolling

        val canShowPaymentError: Boolean
            get() = !isPaymentInProgress

        val canShowPaymentButton: Boolean
            get() {
                // Не показываем кнопку, если платеж уже оплачен или успешно завершен
                if (isPaymentPaid == true || paymentStatus == PaymentStatus.SUCCEEDED) {
                    return false
                }

                // Для статуса PENDING показываем кнопку "Повторить оплату" даже если polling активен
                // Это позволяет пользователю повторить попытку оплаты, если что-то пошло не так
                // (особенно важно для iOS "умного платежа", где polling может продолжаться долго)
                if (paymentStatus == PaymentStatus.PENDING) {
                    // Не показываем кнопку только если идет начальная загрузка или обработка платежа
                    // Но показываем, если только polling активен
                    return !isPaymentLoading && !isPaymentProcessing
                }

                // Для других статусов не показываем кнопку, если идет процесс оплаты
                if (isPaymentInProgress) {
                    return false
                }

                // Показываем кнопку, если статус null, UNKNOWN или CANCELED
                // (REFUNDED не показываем, так как платеж уже возвращен)
                return paymentStatus == null ||
                        paymentStatus == PaymentStatus.UNKNOWN ||
                        paymentStatus == PaymentStatus.CANCELED
            }

        val paymentCanBeChanged: Boolean
            get() {
                val canChangeByStatus = incomingOrder?.status == DeliveryStatus.UNCONFIRMED
                        || incomingOrder?.status == DeliveryStatus.WAIT_COOKING
                        || incomingOrder?.status == DeliveryStatus.READY_FOR_COOKING
                        || incomingOrder?.status == DeliveryStatus.COOKING_STARTED

                if (!canChangeByStatus) return false

                // Для онлайн-оплаты можно менять только если заказ еще не оплачен
                val isOnline = incomingOrder.isOnlinePayment(paymentMethodCodeFromNav)

                return if (isOnline) {
                    isPaymentPaid != true && paymentStatus != PaymentStatus.SUCCEEDED
                } else {
                    false
                }
            }
    }
}

