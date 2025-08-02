package com.mandarinkafe.mandarin.features.order.data.mapper

object OrderConstants {
    const val PHONE_PREFIX = "+7"
    const val DELIVERY_TYPE_DELIVERY = "DeliveryByCourier"
    const val DELIVERY_TYPE_PICKUP = "DeliveryByClient"
    const val CUSTOMER_TYPE_ONE_TIME = "one-time"
    const val ADDRESS_TYPE_LEGACY = "legacy"
    const val ADDRESS_TYPE_CITY = "city"
    const val PAYMENT_CASH_CODE = "CASH"
    const val FULL_PERCENT = 100
    const val FULL_PERCENT_DOUBLE = 100.0
    const val PRICE_DECIMALS = 2
    const val DEFAULT_AMOUNT = 1.0

    // Комментарии
    const val NO_UTENSILS_COMMENT = "БЕЗ ПРИБОРОВ"
    const val UTENSILS_NEED_PREFIX = "НУЖНЫ: "
    const val NO_CHANGE_COMMENT = "Без сдачи"
    const val CHANGE_FROM_COMMENT_PREFIX = "НУЖНА СДАЧА С "
    const val DISCOUNT_APPLIED = "Применена скидка "
    const val DISCOUNT_PERCENT = "%"
    const val DIVIDER_FOR_USER_COMMENT = " || "
    const val DIVIDER_FOR_TECH_PART = ". "
}