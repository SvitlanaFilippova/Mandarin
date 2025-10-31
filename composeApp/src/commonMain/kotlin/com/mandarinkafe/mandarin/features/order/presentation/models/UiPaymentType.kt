package com.mandarinkafe.mandarin.features.order.presentation.models

import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_BANK_CODE
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_CASH_CODE
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_ONLINE_CODE
import dev.icerock.moko.resources.StringResource

enum class UiPaymentType(
    val code: String,
    val nameRes: StringResource,
) {
    ONLINE(
        code = PAYMENT_ONLINE_CODE,
        nameRes = MR.strings.payment_type_online
    ),
    CASH(
        code = PAYMENT_CASH_CODE,
        nameRes = MR.strings.payment_type_cash
    ),
    BANK(
        code = PAYMENT_BANK_CODE,
        nameRes = MR.strings.payment_type_card
    );

    companion object {
        fun fromCode(code: String): UiPaymentType? =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) }

    }
}

fun PaymentType.toUi(): UiPaymentType? {
    return UiPaymentType.entries.firstOrNull { it.code == this.code }
}

fun List<PaymentType>.toUI() = mapNotNull { UiPaymentType.fromCode(it.code) }

fun UiPaymentType.toDomain(): PaymentType {
    return PaymentType(
        id = this.name,
        code = this.code,
        paymentTypeKind = this.name
    )
}