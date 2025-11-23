package com.mandarinkafe.mandarin.features.orderinfo.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingPaymentDto
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.ChangeOrderRepository
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class ChangeOrderRepositoryImpl(private val networkClient: IikoNetworkClient) :
    ChangeOrderRepository {
    override suspend fun cancel(
        id: String,
        cancelCauseId: String?,
        cancelComment: String?,
    ): Resource<Unit> {
        val response = try {
            networkClient.cancelOrder(id, cancelCauseId, cancelComment)
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
            networkClient.addPayments(orderId, payment)
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
}