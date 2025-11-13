package com.mandarinkafe.mandarin.features.payment.data.impl

import com.mandarinkafe.mandarin.features.payment.data.dto.CancelPaymentRequest
import com.mandarinkafe.mandarin.features.payment.data.dto.CreatePaymentRequest
import com.mandarinkafe.mandarin.features.payment.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.payment.data.network.PaymentNetworkClient
import com.mandarinkafe.mandarin.features.payment.domain.api.PaymentRepository
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentInfo
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier

class PaymentRepositoryImpl(
    private val networkClient: PaymentNetworkClient,
) : PaymentRepository {

    override suspend fun createPayment(
        paymentToken: String,
        orderId: String,
        amount: Double,
        currency: String,
        description: String,
    ): Resource<PaymentInfo> {
        return try {
            val request = CreatePaymentRequest(
                payment_token = paymentToken,
                order_id = orderId,
                amount = amount,
                currency = currency,
                description = description
            )
            val response = networkClient.createPayment(request)

            when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet()

                HTTP_SUCCESS -> {
                    val paymentInfo = PaymentInfo(
                        paymentId = response.id,
                        orderId = orderId,
                        status = com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus.fromString(
                            response.status
                        ),
                        paid = response.paid ?: false,
                        amountValue = response.amount?.value,
                        amountCurrency = response.amount?.currency,
                        description = response.description,
                        createdAt = response.created_at,
                        updatedAt = null,
                        confirmationUrl = response.confirmation?.confirmation_url
                    )
                    Resource.Success(data = paymentInfo)
                }

                else -> {
                    Napier.e("PaymentRepositoryImpl.createPayment: Server error ${response.resultCode}")
                    Resource.ErrorOther("Ошибка создания платежа")
                }
            }
        } catch (e: Exception) {
            Napier.e("PaymentRepositoryImpl.createPayment: Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override suspend fun getPaymentStatus(orderId: String): Resource<PaymentInfo> {
        return try {
            val response = networkClient.getPaymentStatus(orderId)

            when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet()

                HTTP_SUCCESS -> {
                    val paymentInfo = response.toDomain()
                    Resource.Success(data = paymentInfo)
                }

                404 -> {
                    Resource.ErrorOther("Платеж не найден")
                }

                else -> {
                    Napier.e("PaymentRepositoryImpl.getPaymentStatus: Server error ${response.resultCode}")
                    Resource.ErrorOther("Ошибка получения статуса платежа")
                }
            }
        } catch (e: Exception) {
            Napier.e("PaymentRepositoryImpl.getPaymentStatus: Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    override suspend fun cancelPayment(orderId: String): Resource<Boolean> {
        return try {
            val request = CancelPaymentRequest(order_id = orderId)
            val response = networkClient.cancelPayment(request)

            when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet()

                HTTP_SUCCESS -> {
                    Resource.Success(data = response.success ?: false)
                }

                else -> {
                    Napier.e("PaymentRepositoryImpl.cancelPayment: Server error ${response.resultCode}")
                    Resource.ErrorOther(response.message ?: "Ошибка отмены платежа")
                }
            }
        } catch (e: Exception) {
            Napier.e("PaymentRepositoryImpl.cancelPayment: Exception", e)
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }
}

