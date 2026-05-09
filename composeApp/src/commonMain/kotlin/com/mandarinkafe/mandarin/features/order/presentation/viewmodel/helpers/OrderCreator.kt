package com.mandarinkafe.mandarin.features.order.presentation.viewmodel.helpers

import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.order.domain.api.CreateOrderUseCase
import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.features.order.presentation.mapper.IikoErrorFormatter
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetOrderStatusFromIikoUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.tickerFlow
import dev.icerock.moko.resources.StringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

class OrderCreator(
    private val createOrder: CreateOrderUseCase,
    private val getOrderStatus: GetOrderStatusFromIikoUseCase,
) {
    private var observeJob: Job? = null

    suspend fun submit(
        scope: CoroutineScope,
        order: OutgoingOrder,
        onSuccess: (IncomingOrder) -> Unit,
        onError: (StringResource, String?) -> Unit,
        onLoading: () -> Unit,
    ) {
        when (val result = createOrder(order)) {
            is Resource.Success -> {
                val orderInfo = result.data
                val status = orderInfo?.creationStatus

                when (status) {
                    CreationStatus.IN_PROGRESS -> {
                        onLoading()
                        observeOrderUntilSuccess(scope, orderInfo.id, onSuccess, onError, onLoading)
                    }

                    CreationStatus.SUCCESS -> onSuccess(orderInfo)
                    CreationStatus.ERROR -> {
                        val errorDetails = buildErrorDetails(orderInfo.errorInfo)
                        onError(MR.strings.error_order_creation_failed, errorDetails)
                    }

                    null -> onError(
                        MR.strings.error_empty_server_response,
                        null,
                    )
                }
            }

            is Resource.ErrorNoInternet -> onError(
                MR.strings.error_no_internet,
                null,
            )

            else -> {
                val errorDetails = result.message
                onError(MR.strings.error_order_creation_failed, errorDetails)
            }
        }
    }

    private fun observeOrderUntilSuccess(
        scope: CoroutineScope,
        orderId: String?,
        onSuccess: (IncomingOrder) -> Unit,
        onError: (StringResource, String?) -> Unit,
        onLoading: () -> Unit,
    ) {
        Napier.d("HISTORY DEBUG - observeOrderUntilSuccess started")
        stopObserving()

        if (!validateOrderId(orderId, onError)) return

        observeJob = scope.launch {
            val isCompleted = withTimeoutOrNull(15.seconds) {
                tickerFlow(period = ORDER_STATUS_UPD_DELAY.seconds)
                    .onStart { emit(Unit) }
                    .map { getOrderStatus(orderId!!) }
                    .collect { result ->
                        handleOrderStatusResult(result, onSuccess, onError, onLoading)
                    }
                true
            }

            handleTimeout(isCompleted, onError)
        }
    }

    private fun validateOrderId(
        orderId: String?,
        onError: (StringResource, String?) -> Unit,
    ): Boolean {
        if (orderId.isNullOrBlank()) {
            onError(MR.strings.error_order_creation_failed, null)
            return false
        }
        return true
    }

    private fun handleOrderStatusResult(
        result: Resource<IncomingOrder>,
        onSuccess: (IncomingOrder) -> Unit,
        onError: (StringResource, String?) -> Unit,
        onLoading: () -> Unit,
    ) {
        when (result) {
            is Resource.Success -> handleCreationStatus(result, onSuccess, onError, onLoading)
            is Resource.ErrorNoInternet -> {
                onError(MR.strings.error_no_internet, null)
                stopObserving()
            }

            is Resource.ErrorOther -> {
                onError(MR.strings.error_order_status_failed, result.message)
                stopObserving()
            }

            else -> Unit
        }
    }

    private fun handleCreationStatus(
        result: Resource.Success<IncomingOrder>,
        onSuccess: (IncomingOrder) -> Unit,
        onError: (StringResource, String?) -> Unit,
        onLoading: () -> Unit,
    ) {
        when (result.data?.creationStatus) {
            CreationStatus.SUCCESS -> {
                onSuccess(result.data)
                stopObserving()
            }

            CreationStatus.ERROR -> {
                val errorDetails = buildErrorDetails(result.data.errorInfo)
                onError(MR.strings.error_order_creation_failed, errorDetails)
                Napier.e("ERROR creating order: $errorDetails")
                stopObserving()
            }

            else -> onLoading()
        }
    }

    private fun handleTimeout(isCompleted: Boolean?, onError: (StringResource, String?) -> Unit) {
        if (isCompleted == null) {
            onError(MR.strings.error_order_timeout, null)
            stopObserving()
        }
    }

    fun stopObserving() {
        observeJob?.cancel()
        observeJob = null
    }

    private fun buildErrorDetails(errorInfo: com.mandarinkafe.mandarin.features.order.domain.models.ErrorInfo?): String? {
        if (errorInfo == null) return null

        return buildString {
            errorInfo.message?.let { message ->
                append(IikoErrorFormatter.format(message))
            }
            errorInfo.errorReason?.let { reason ->
                if (isNotEmpty()) append("\n")
                append("Причина: $reason")
            }
            errorInfo.code.takeIf { it.isNotBlank() }?.let { code ->
                if (isNotEmpty()) append("\n")
                append("Код ошибки: $code")
            }
        }.takeIf { it.isNotEmpty() }
    }

    private companion object {
        const val ORDER_STATUS_UPD_DELAY = 1
    }
}

