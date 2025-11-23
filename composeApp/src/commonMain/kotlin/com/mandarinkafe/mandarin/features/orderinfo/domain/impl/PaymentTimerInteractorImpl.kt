package com.mandarinkafe.mandarin.features.orderinfo.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.CancelOrderUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetOrderStatusUseCase
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.PaymentTimerInteractor
import com.mandarinkafe.mandarin.features.orderinfo.domain.isOnlinePayment
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PaymentTimerInteractorImpl(
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val getOrderStatusUseCase: GetOrderStatusUseCase,
    private val coroutineScope: CoroutineScope,
) : PaymentTimerInteractor {

    private var timerJob: Job? = null

    companion object {
        private const val ORDER_STATUS_UPD_DELAY_AFTER_CANCEL = 500L
        private const val MILLISECONDS_PER_SECOND = 1000
        private const val TIMEOUT_GRACE_PERIOD_MS = 2000L
        private const val AUTO_CANCEL_CAUSE_ID = "15c16410-972a-402c-96f2-402ee4c05d21"
        private const val AUTO_CANCEL_COMMENT =
            "Онлайн-оплата не была вовремя произведена. Заказ отменён автоматически."
    }

    override fun startTimer(
        paymentDeadline: Long,
        orderId: String,
        onTimeUpdate: (Int) -> Unit,
        shouldStopTimer: () -> Boolean,
        onTimeout: suspend () -> Unit,
    ): Job {
        stopTimer()

        timerJob = coroutineScope.launch {
            var shouldContinue = true
            while (shouldContinue) {
                val currentTime = Clock.System.now().toEpochMilliseconds()
                val remainingMillis = paymentDeadline - currentTime
                val remainingSeconds =
                    (remainingMillis / MILLISECONDS_PER_SECOND).toInt().coerceAtLeast(0)

                onTimeUpdate(remainingSeconds)

                if (remainingSeconds <= 0) {
                    onTimeUpdate(0)
                    onTimeout()
                    shouldContinue = false
                } else if (shouldStopTimer()) {
                    shouldContinue = false
                } else {
                    delay(Constants.DELAY_1_SECOND)
                }
            }
        }

        return timerJob!!
    }

    override fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun canCancelOrder(
        order: IncomingOrder?,
        paymentStatus: PaymentStatus?,
        isPaymentPaid: Boolean?,
        paymentMethodCodeFromNav: String?,
    ): Boolean {
        // Проверяем, что заказ не оплачен и не закрыт
        if (paymentStatus == PaymentStatus.SUCCEEDED ||
            isPaymentPaid == true ||
            order?.isClosed == true
        ) {
            return false
        }

        // Проверяем, что способ оплаты онлайн
        return order.isOnlinePayment(paymentMethodCodeFromNav)
    }

    override suspend fun autoCancelOnTimeout(
        orderId: String,
        canCancel: () -> Boolean,
        onCancelStarted: () -> Unit,
        onCancelCompleted: suspend (String) -> Unit,
        onCancelFailed: () -> Unit,
    ) {
        if (!canCancel()) {
            return
        }

        onCancelStarted()

        // Ждем 2 секунды на случай, если оплата пришла в последний момент
        delay(TIMEOUT_GRACE_PERIOD_MS)

        // Повторно проверяем статус перед отменой
        if (!canCancel()) {
            onCancelFailed()
            return
        }

        // Выполняем отмену с указанными параметрами
        val cancelResult = cancelOrderUseCase.invoke(
            id = orderId,
            cancelCauseId = AUTO_CANCEL_CAUSE_ID,
            cancelComment = AUTO_CANCEL_COMMENT
        )

        if (cancelResult is Resource.Success) {
            // Обновляем статус заказа после успешной отмены
            delay(ORDER_STATUS_UPD_DELAY_AFTER_CANCEL)
            getOrderStatusUseCase(orderId)
            onCancelCompleted(orderId)
        } else {
            // При ошибке повторяем попытку через 2 секунды (один раз)
            delay(TIMEOUT_GRACE_PERIOD_MS)

            // Повторно проверяем статус
            if (!canCancel()) {
                onCancelFailed()
                return
            }

            // Повторная попытка отмены
            val retryResult = cancelOrderUseCase.invoke(
                id = orderId,
                cancelCauseId = AUTO_CANCEL_CAUSE_ID,
                cancelComment = AUTO_CANCEL_COMMENT
            )

            if (retryResult is Resource.Success) {
                delay(ORDER_STATUS_UPD_DELAY_AFTER_CANCEL)
                getOrderStatusUseCase(orderId)
                onCancelCompleted(orderId)
            } else {
                // Если и повторная попытка не удалась, сбрасываем флаг
                // Сервер сам отменит заказ
                onCancelFailed()
            }
        }
    }
}

