package com.mandarinkafe.mandarin.features.payment.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePaymentResponse(
    val id: String? = null,
    val status: String? = null,
    val paid: Boolean? = null,
    val amount: PaymentAmountDto? = null,
    val description: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    val confirmation: PaymentConfirmationDto? = null,
    @SerialName("payment_method_type")
    val paymentMethodType: String? = null, // Тип платежного метода от Юкассы (bank_card, sbp, sberbank и т.д.)
) : Response()


