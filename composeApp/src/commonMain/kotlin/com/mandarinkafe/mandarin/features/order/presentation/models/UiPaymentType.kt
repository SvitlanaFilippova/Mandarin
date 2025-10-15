package com.mandarinkafe.mandarin.features.order.presentation.models

import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_BANK_CODE
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_CASH_CODE
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_ONLINE_CODE

enum class UiPaymentType(
    val code: String,
    val nameRes: String
) {
    ONLINE(
        code = PAYMENT_ONLINE_CODE,
        nameRes = "Онлайн"
    ),
    CASH(
        code = PAYMENT_CASH_CODE,
        nameRes = "Наличные"
    ),
    BANK(
        code = PAYMENT_BANK_CODE,
        nameRes = "Банковская карта"
    )
}

fun PaymentType.toUi(): UiPaymentType? {
    return UiPaymentType.entries.firstOrNull { it.code == this.code }
}

fun UiPaymentType.toDomain(): PaymentType {
    return PaymentType(
        id = this.name,
        code = this.code,
        paymentTypeKind = this.name
    )
}