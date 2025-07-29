package com.mandarinkafe.mandarin.features.order.data.mapper

object OrderConstants {
    const val PHONE_PREFIX = "+7"
    const val DELIVERY_TYPE_DELIVERY = "DeliveryByCourier"
    const val DELIVERY_TYPE_PICKUP = "DeliveryByClient"
    const val CUSTOMER_TYPE_ONE_TIME = "one-time"
    const val ITEM_TYPE_PRODUCT = "Product"
    const val ITEM_TYPE_COMPOUND = "Compound"
    const val ADDRESS_TYPE_LEGACY = "legacy"
    const val PAYMENT_CASH_CODE = "CASH"

    // Комментарии
    const val NO_UTENSILS_COMMENT = "БЕЗ ПРИБОРОВ"
    const val UTENSILS_NEED_PREFIX = "НУЖНЫ: "
    const val NO_CHANGE_COMMENT = "Без сдачи"
    const val CHANGE_FROM_COMMENT_PREFIX = "НУЖНА СДАЧА С "
}