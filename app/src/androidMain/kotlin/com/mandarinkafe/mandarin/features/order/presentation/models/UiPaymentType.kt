package com.mandarinkafe.mandarin.features.order.presentation.models

import androidx.annotation.StringRes
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_BANK_CODE
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_CASH_CODE
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_ONLINE_CODE

enum class UiPaymentType(
    val code: String,
    @StringRes val nameRes: Int
) {
    ONLINE(
        code = PAYMENT_ONLINE_CODE,
        nameRes = R.string.payment_online,
    ),
    BANK(
        code = PAYMENT_BANK_CODE,
        nameRes = R.string.payment_self_card,
    ),
    CASH(
        code = PAYMENT_CASH_CODE,
        nameRes = R.string.payment_cash
    );

    companion object {
        fun fromCode(code: String): UiPaymentType? =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) }

    }
}

fun List<PaymentType>.toUI() = mapNotNull { UiPaymentType.fromCode(it.code) }
