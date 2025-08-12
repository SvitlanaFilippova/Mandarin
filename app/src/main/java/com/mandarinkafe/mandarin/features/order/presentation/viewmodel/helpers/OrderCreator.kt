package com.mandarinkafe.mandarin.features.order.presentation.viewmodel.helpers

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.order.domain.api.CreateOrderUseCase
import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ObserveOrderStatusUseCase
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class OrderCreator @Inject constructor(
    private val createOrder: CreateOrderUseCase,
    private val observeStatus: ObserveOrderStatusUseCase
) {
    private var observeJob: Job? = null

    suspend fun submit(
        scope: CoroutineScope,
        order: OutgoingOrder,
        onSuccess: (IncomingOrder) -> Unit,
        onError: (String) -> Unit,
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
                        onError(orderInfo.errorInfo?.message ?: "Не удалось создать заказ")

                    null -> onError("Ошибка: пустой ответ от сервера")
                }
            }

            is Resource.ErrorNoInternet -> onError("Нет подключения к интернету")
            else -> onError(result.message ?: "Не удалось отправить заказ")
        }
    }

    private fun observeOrderUntilSuccess(
        scope: CoroutineScope,
        orderId: String,
        onSuccess: (IncomingOrder) -> Unit,
        onError: (String) -> Unit,
        onLoading: () -> Unit
    ) {
        stopObserving()
        observeJob = scope.launch {
            observeStatus(orderId, ORDER_STATUS_UPD_DELAY).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        when (result.data?.creationStatus) {
                            CreationStatus.SUCCESS -> {
                                onSuccess(result.data)
                                stopObserving()
                            }

                            CreationStatus.ERROR -> {
                                onError(
                                    result.data.errorInfo?.message ?: "Не удалось создать заказ"
                                )
                                stopObserving()
                            }

                            else -> onLoading()
                        }
                    }

                    is Resource.ErrorNoInternet -> onError("Нет подключения к интернету")
                    is Resource.ErrorOther -> onError(result.message ?: "Ошибка получения статуса")
                    else -> Unit
                }
            }
        }
    }

    fun stopObserving() {
        observeJob?.cancel()
        observeJob = null
    }

    private companion object {
        const val ORDER_STATUS_UPD_DELAY = 1000L
    }
}