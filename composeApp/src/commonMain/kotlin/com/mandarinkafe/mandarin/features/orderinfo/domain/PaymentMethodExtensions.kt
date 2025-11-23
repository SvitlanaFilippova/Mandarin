package com.mandarinkafe.mandarin.features.orderinfo.domain

import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_ONLINE_CODE

/**
 * Проверяет, является ли способ оплаты онлайн-оплатой.
 * Использует paymentMethodCode из заказа или fallback значение из навигации.
 * Работает как с nullable, так и с не-nullable IncomingOrder.
 */
fun IncomingOrder?.isOnlinePayment(paymentMethodCodeFromNav: String? = null): Boolean {
    val paymentCode = this?.paymentMethodCode ?: paymentMethodCodeFromNav
    return paymentCode?.equals(PAYMENT_ONLINE_CODE, ignoreCase = true) == true
}

/**
 * Получает код способа оплаты из заказа или fallback значение из навигации.
 */
fun IncomingOrder?.getPaymentMethodCode(paymentMethodCodeFromNav: String? = null): String? {
    return this?.paymentMethodCode ?: paymentMethodCodeFromNav
}

