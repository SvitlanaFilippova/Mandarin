package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Immutable
import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.order.domain.models.ErrorInfo
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingOrderItem

@Immutable
data class IncomingOrder(
    val id: String,
    val number: String?,
    val timestamp: Long,
    val creationStatus: CreationStatus,
    val errorInfo: ErrorInfo?,
    val phone: String? = null,
    val deliveryAddress: Address? = null,
    val comment: String? = null,
    val customerName: String? = null,
    val items: List<IncomingOrderItem>,
    val paymentName: String? = null,
    val status: DeliveryStatus,
    val cancelInfo: String? = null,
    val orderType: OrderType? = null,
    val processedPaymentsSum: Double? = null,
    val sum: Double? = null,
    val discountReason: String? = null,
    val whenCancelled: String? = null,
    val whenClosed: String? = null,
    val whenConfirmed: String? = null,
    val whenCookingCompleted: String? = null,
    val whenCreated: String? = null,
    val whenDelivered: String? = null,
    val whenSent: String? = null,
    val isDelivery: Boolean,
) {
    val isClosed: Boolean
        get() = status == DeliveryStatus.CANCELLED || status == DeliveryStatus.CLOSED
    val canBeCanceled: Boolean
        get() = status == DeliveryStatus.UNCONFIRMED || status == DeliveryStatus.WAIT_COOKING || status == DeliveryStatus.READY_FOR_COOKING
}
