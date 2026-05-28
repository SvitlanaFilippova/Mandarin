package com.mandarinkafe.mandarin.features.orderinfo.data.impl

import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.order.data.network.ServerAddPaymentRequest
import com.mandarinkafe.mandarin.features.order.data.network.ServerCancelOrderRequest
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingPaymentDto
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangeOrderRepository
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersHistoryServerApi
import com.mandarinkafe.mandarin.util.Constants.BEARER_TOKEN_TYPE
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class ChangeOrderRepositoryImpl(
    private val serverApi: OrdersHistoryServerApi,
    private val authRepository: AuthRepository,
) : ChangeOrderRepository {
    override suspend fun cancel(
        id: String,
        cancelCauseId: String?,
        cancelComment: String?,
    ): Resource<Unit> {
        val token = authRepository.getAccessToken()
            ?: return Resource.ErrorOther("Токен авторизации не найден")
        val response = try {
            serverApi.cancelOrder(
                token = buildAuthToken(token),
                orderId = id,
                body = ServerCancelOrderRequest(
                    cancelCauseId = cancelCauseId,
                    cancelComment = cancelComment,
                ),
            )
        } catch (e: Exception) {
            return Resource.ErrorOther("Ошибка сети: ${e.message}")
        }
        return when (response.resultCode) {
            HTTP_SUCCESS -> Resource.Success(Unit)

            else -> Resource.ErrorOther("Что-то пошло не так")
        }
    }

    override suspend fun addPayment(
        orderId: String,
        paymentTypeId: String,
        amount: Double,
    ): Resource<Unit> {
        val token = authRepository.getAccessToken()
            ?: return Resource.ErrorOther("Токен авторизации не найден")
        // Создаем платеж для онлайн-оплаты
        val payment = OutgoingPaymentDto(
            paymentTypeKind = "card",
            sum = amount,
            paymentTypeId = paymentTypeId,
            isPrepay = false,
            isProcessedExternally = true,
            isFiscalizedExternally = false
        )

        val response = try {
            serverApi.addPayment(
                token = buildAuthToken(token),
                orderId = orderId,
                body = ServerAddPaymentRequest(payment),
            )
        } catch (e: Exception) {
            return Resource.ErrorOther("Ошибка сети: ${e.message}")
        }

        return when (response.resultCode) {
            HTTP_SUCCESS -> {
                Resource.Success(Unit)
            }

            NO_CONNECTION -> {
                Resource.ErrorNoInternet()
            }

            else -> {
                Resource.ErrorOther("Не удалось добавить платеж в iiko")
            }
        }
    }

    private fun buildAuthToken(token: String): String = "$BEARER_TOKEN_TYPE $token"
}
