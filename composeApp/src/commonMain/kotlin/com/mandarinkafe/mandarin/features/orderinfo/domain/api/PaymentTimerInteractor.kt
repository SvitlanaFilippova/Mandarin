package com.mandarinkafe.mandarin.features.orderinfo.domain.api

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import kotlinx.coroutines.Job

/**
 * Интерфейс для управления таймером оплаты и автоматической отменой заказа.
 */
interface PaymentTimerInteractor {
    /**
     * Запускает таймер оплаты для заказа с указанным дедлайном.
     * @param paymentDeadline дедлайн оплаты в миллисекундах
     * @param orderId ID заказа
     * @param onTimeUpdate callback для обновления оставшегося времени (в секундах)
     * @param shouldStopTimer callback для проверки, нужно ли остановить таймер
     * @param onTimeout callback, вызываемый при истечении времени
     * @return Job таймера для возможности его отмены
     */
    fun startTimer(
        paymentDeadline: Long,
        orderId: String,
        onTimeUpdate: (Int) -> Unit,
        shouldStopTimer: () -> Boolean,
        onTimeout: suspend () -> Unit,
    ): Job

    /**
     * Останавливает таймер оплаты.
     */
    fun stopTimer()

    /**
     * Проверяет, можно ли отменять заказ (не оплачен, не закрыт, онлайн-оплата).
     */
    fun canCancelOrder(
        order: IncomingOrder?,
        paymentStatus: PaymentStatus?,
        isPaymentPaid: Boolean?,
        paymentMethodCodeFromNav: String?,
    ): Boolean

    /**
     * Выполняет автоматическую отмену заказа при истечении таймера.
     * @param orderId ID заказа
     * @param canCancel callback для проверки, можно ли отменять заказ
     * @param onCancelStarted callback, вызываемый при начале отмены
     * @param onCancelCompleted callback, вызываемый при успешной отмене
     * @param onCancelFailed callback, вызываемый при неудачной отмене
     */
    suspend fun autoCancelOnTimeout(
        orderId: String,
        canCancel: () -> Boolean,
        onCancelStarted: () -> Unit,
        onCancelCompleted: suspend (String) -> Unit,
        onCancelFailed: () -> Unit,
    )
}

