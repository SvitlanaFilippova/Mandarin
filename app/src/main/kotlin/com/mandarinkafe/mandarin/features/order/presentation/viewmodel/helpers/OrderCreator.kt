package com.mandarinkafe.mandarin.features.order.presentation.viewmodel.helpers

import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.order.domain.api.CreateOrderUseCase
import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.GetOrderStatusUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.UiText
import com.mandarinkafe.mandarin.util.tickerFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

class OrderCreator(
    private val createOrder: CreateOrderUseCase,
    private val getOrderStatus: GetOrderStatusUseCase
) {
    private var observeJob: Job? = null

    suspend fun submit(
        scope: CoroutineScope,
        order: OutgoingOrder,
        onSuccess: (IncomingOrder) -> Unit,
        onError: (UiText) -> Unit,
        onLoading: () -> Unit
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
                    CreationStatus.ERROR ->
                        onError(
                            orderInfo.errorInfo?.message?.let { UiText.DynamicString(it) }
                                ?: UiText.StringResource(R.string.error_order_creation_failed)
                        )

                    null -> onError(
                        UiText.StringResource(R.string.error_empty_server_response)
                    )
                }
            }

            is Resource.ErrorNoInternet -> onError(
                UiText.StringResource(R.string.error_no_internet)
            )

            else -> onError(
                result.message?.let { UiText.DynamicString(it) }
                    ?: UiText.StringResource(R.string.error_order_creation_failed)
            )
        }
    }

    private fun observeOrderUntilSuccess(
        scope: CoroutineScope,
        orderId: String?,
        onSuccess: (IncomingOrder) -> Unit,
        onError: (UiText) -> Unit,
        onLoading: () -> Unit
    ) {
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

    private fun validateOrderId(orderId: String?, onError: (UiText) -> Unit): Boolean {
        if (orderId.isNullOrBlank()) {
            onError(UiText.StringResource(R.string.error_order_creation_failed))
            return false
        }
        return true
    }

    private fun handleOrderStatusResult(
        result: Resource<IncomingOrder>,
        onSuccess: (IncomingOrder) -> Unit,
        onError: (UiText) -> Unit,
        onLoading: () -> Unit
    ) {
        when (result) {
            is Resource.Success -> handleCreationStatus(result, onSuccess, onError, onLoading)
            is Resource.ErrorNoInternet -> {
                onError(UiText.StringResource(R.string.error_no_internet))
                stopObserving()
            }

            is Resource.ErrorOther -> {
                onError(
                    result.message?.let { UiText.DynamicString(it) }
                        ?: UiText.StringResource(R.string.error_order_status_failed)
                )
                stopObserving()
            }

            else -> Unit
        }
    }

    private fun handleCreationStatus(
        result: Resource.Success<IncomingOrder>,
        onSuccess: (IncomingOrder) -> Unit,
        onError: (UiText) -> Unit,
        onLoading: () -> Unit
    ) {
        when (result.data?.creationStatus) {
            CreationStatus.SUCCESS -> {
                onSuccess(result.data)
                stopObserving()
            }

            CreationStatus.ERROR -> {
                onError(
                    result.data.errorInfo?.message?.let { UiText.DynamicString(it) }
                        ?: UiText.StringResource(R.string.error_order_creation_failed)
                )
                stopObserving()
            }

            else -> onLoading()
        }
    }

    private fun handleTimeout(isCompleted: Boolean?, onError: (UiText) -> Unit) {
        if (isCompleted == null) {
            onError(UiText.StringResource(R.string.error_order_timeout))
            stopObserving()
        }
    }

    fun stopObserving() {
        observeJob?.cancel()
        observeJob = null
    }

    private companion object {
        const val ORDER_STATUS_UPD_DELAY = 1
    }
}
