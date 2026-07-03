package com.mandarinkafe.mandarin.features.ordershistory.domain.models

import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.ErrorInfo
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus

data class SavedOrder(
    val id: String, // внутренний ID заказа
    val number: String = "", // номер заказа, который видят операторы в терминале
    val timestamp: Long,
    val whenCreated: String = "",
    val orderType: DeliveryType?,
    val addressLine1: String = "",
    val addressDetails: String = "",
    val mealNames: String = "",
    val status: DeliveryStatus? = null,
    val creationStatus: CreationStatus? = null,
    val errorInfo: ErrorInfo? = null,
    val paymentMethodCode: String? = null, // Код способа оплаты ("CARD", "CASH", "BANK")
    val mealIds: List<String> = emptyList(), // Список id базовых блюд (без добавок и модификаторов)
) {
    val isActive: Boolean
        get() = creationStatus != CreationStatus.ERROR &&
                status != DeliveryStatus.CANCELLED &&
                status != DeliveryStatus.CLOSED
}
