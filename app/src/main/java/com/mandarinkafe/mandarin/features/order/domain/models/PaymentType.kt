package com.mandarinkafe.mandarin.features.order.domain.models

import com.mandarinkafe.mandarin.R

enum class PaymentType(val nameRes: Int) {
    ONLINE(R.string.payment_online),
    BANK(R.string.payment_self_card),
    CASH(R.string.payment_cash),
}

