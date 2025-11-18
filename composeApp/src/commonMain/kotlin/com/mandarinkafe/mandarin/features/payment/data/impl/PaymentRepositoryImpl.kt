package com.mandarinkafe.mandarin.features.payment.data.impl

import com.mandarinkafe.mandarin.features.payment.data.dto.CancelPaymentRequest
import com.mandarinkafe.mandarin.features.payment.data.dto.CreatePaymentRequest
import com.mandarinkafe.mandarin.features.payment.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.payment.data.network.PaymentNetworkClient
import com.mandarinkafe.mandarin.features.payment.domain.api.PaymentRepository
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentInfo
import com.mandarinkafe.mandarin.util.Constants.HTTP_NOT_FOUND
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class PaymentRepositoryImpl(
    private val networkClient: PaymentNetworkClient,
) : PaymentRepository {

    override suspend fun createPayment(
        paymentToken: String,
        orderId: String,
        amount: Double,
        currency: String,
        description: String,
        returnUrl: String?,
    ): Resource<PaymentInfo> {
        return try {
            val request = CreatePaymentRequest(
                paymentToken = paymentToken,
                orderId = orderId,
                amount = amount,
                currency = currency,
                description = description,
                returnUrl = returnUrl
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
                        createdAt = response.createdAt,
                        updatedAt = null,
                        confirmationUrl = response.confirmation?.confirmationUrl
                    )
                    Resource.Success(data = paymentInfo)
                }

                else -> {
                    Resource.ErrorOther(ERROR_CREATE_PAYMENT)
                }
            }
        } catch (e: Exception) {
            Resource.ErrorOther(ERROR_PREFIX + { e.message })
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

                HTTP_NOT_FOUND -> {
                    Resource.ErrorOther("Платеж не найден")
                }

                else -> {
                    Resource.ErrorOther(ERROR_GET_PAYMENT_STATUS)
                }
            }
        } catch (e: Exception) {
            Resource.ErrorOther(ERROR_PREFIX + { e.message })
        }
    }

    override suspend fun cancelPayment(orderId: String): Resource<Boolean> {
        return try {
            val request = CancelPaymentRequest(orderId = orderId)
            val response = networkClient.cancelPayment(request)

            when (response.resultCode) {
                NO_CONNECTION -> {
                    Resource.ErrorNoInternet()
                }

                HTTP_SUCCESS -> {
                    val success = response.success ?: false
                    Resource.Success(data = success)
                }

                else -> {
                    Resource.ErrorOther(response.message ?: ERROR_CANCEL_PAYMENT)
                }
            }
        } catch (e: Exception) {
            Resource.ErrorOther(ERROR_PREFIX + { e.message })
        }
    }

    private companion object {
        private const val ERROR_PREFIX = "Ошибка: "
        private const val ERROR_CREATE_PAYMENT = "Ошибка создания платежа"
        private const val ERROR_GET_PAYMENT_STATUS = "Ошибка получения статуса платежа"
        private const val ERROR_CANCEL_PAYMENT = "Ошибка отмены платежа"
    }
}

