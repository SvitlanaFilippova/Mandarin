package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Immutable
import com.mandarinkafe.mandarin.core.data.dto.order.CustomerDto
import com.mandarinkafe.mandarin.core.data.dto.order.OrderType
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants
import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.order.domain.models.ErrorInfo
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.Problem
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
    val customer: CustomerDto? = null,
    val items: List<IncomingOrderItem>,
    val paymentName: String? = null,
    val status: DeliveryStatus,
    val cancelInfo: String? = null,
    val orderType: OrderType? = null,
    val processedPaymentsSum: Int? = null,
    val sum: Double? = null,
    val whenCancelled: String? = null,
    val whenClosed: String? = null,
    val whenConfirmed: String? = null,
    val whenCookingCompleted: String? = null,
    val whenCreated: String? = null,
    val whenDelivered: String? = null,
    val whenPacked: String? = null,
    val whenPrinted: String? = null,
    val whenSent: String? = null,
    val problem: Problem? = null,
) {
    val isClosed: Boolean
        get() = status == DeliveryStatus.CANCELLED || status == DeliveryStatus.CLOSED || status == DeliveryStatus.DELIVERED
    val needToConfirm: Boolean
        get() = status == DeliveryStatus.UNCONFIRMED
    val isDelivery: Boolean
        get() = orderType?.orderServiceType == OrderConstants.DELIVERY_TYPE_DELIVERY
}
