package com.mandarinkafe.mandarin.features.order.data.mapper

object OrderConstants {
    const val PHONE_PREFIX = "+7"
    const val DELIVERY_TYPE_DELIVERY = "DeliveryByCourier"
    const val DELIVERY_TYPE_PICKUP = "DeliveryByClient"
    const val CUSTOMER_TYPE_ONE_TIME = "one-time"
    const val ADDRESS_TYPE_LEGACY = "legacy"
    const val DISCOUNT_TYPE_RMS = "RMS"
    const val PAYMENT_CASH_CODE = "CASH"
    const val PAYMENT_CARD_CODE = "CARD"
    const val PAYMENT_CASH_NAME = "cash"
    const val PAYMENT_CARD_NAME = "card"
    const val DEFAULT_AMOUNT = 1.0

    // Комментарии
    const val NO_UTENSILS_COMMENT = "БЕЗ приборов и салфеток"
    const val UTENSILS_NEED_PREFIX = "Нужны: "
    const val PAYMENT_TYPE_COMMENT_PREFIX = "Способ оплаты: "
    const val NO_CHANGE_COMMENT = "Без сдачи"
    const val CHANGE_FROM_COMMENT_PREFIX = "Нужна сдача с "
    const val DIVIDER_FOR_USER_COMMENT = " || "
    const val DIVIDER_FOR_TECH_PART = ". "
}