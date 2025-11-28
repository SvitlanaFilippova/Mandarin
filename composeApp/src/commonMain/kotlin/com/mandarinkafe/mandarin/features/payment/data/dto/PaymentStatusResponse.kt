package com.mandarinkafe.mandarin.features.payment.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentStatusResponse(
    @SerialName("payment_id")
    val paymentId: String? = null,
    @SerialName("order_id")
    val orderId: String? = null,
    val status: String? = null,
    val paid: Boolean? = null,
    @SerialName("amount_value")
    val amountValue: String? = null,
    @SerialName("amount_currency")
    val amountCurrency: String? = null,
    val description: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    @SerialName("payment_method_type")
    val paymentMethodType: String? = null, // Тип платежного метода от Юкассы (bank_card, sbp, sberbank и т.д.)
) : Response()

