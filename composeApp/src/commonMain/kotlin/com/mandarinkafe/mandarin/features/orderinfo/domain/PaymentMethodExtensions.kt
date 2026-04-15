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

private const val PAYMENT_SUM_COMPARISON_EPSILON = 0.01

/**
 * Заказ оплачен по данным заказа: сумма проведённых оплат покрывает сумму заказа (IIKO / backend).
 */
fun IncomingOrder.isPaidByProcessedSum(): Boolean {
    val orderSum = sum ?: return false
    if (orderSum <= 0.0) return false
    val processed = processedPaymentsSum ?: return false
    return processed + PAYMENT_SUM_COMPARISON_EPSILON >= orderSum
}

