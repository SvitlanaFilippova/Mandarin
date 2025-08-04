package com.mandarinkafe.mandarin.features.orderconfirmation.data

import com.mandarinkafe.mandarin.core.data.dto.order.DeliveryPointDto
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto.DeletionInfoDto
import com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto.IncomingModifierDto
import com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto.IncomingOrderItemDto
import com.mandarinkafe.mandarin.features.orderconfirmation.data.network.dto.OrderInfoResponseDto
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.models.DeletionInfo
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.models.DeliveryStatus
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.models.IncomingModifier
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.models.IncomingOrderItem
import com.mandarinkafe.mandarin.util.DateTimeUtils.toHumanDateTimeOrNull
import com.mandarinkafe.mandarin.util.applyTypography

fun OrderInfoResponseDto.toDomain(): IncomingOrder {
    return IncomingOrder(
        id = id,
        number = order?.number,
        timestamp = timestamp,
        creationStatus = creationStatus?.let { CreationStatus.fromApiName(it) }
            ?: CreationStatus.IN_PROGRESS,
        errorInfo = errorInfo?.toDomain(),
        phone = order?.phone,
        deliveryAddress = order?.deliveryPoint?.toAddress(),
        comment = order?.comment,
        customer = order?.customer,
        items = order?.items?.mapNotNull { it.toDomain() } ?: emptyList(),
        paymentName = order?.payments?.firstOrNull()?.paymentType?.name,
        status = order?.status?.toDeliveryStatus() ?: DeliveryStatus.UNCONFIRMED,
        cancelInfo = order?.cancelInfo?.comment,
        orderType = order?.orderType,
        processedPaymentsSum = order?.processedPaymentsSum,
        sum = order?.sum,
        whenClosed = order?.whenClosed?.toHumanDateTimeOrNull(),
        whenConfirmed = order?.whenConfirmed?.toHumanDateTimeOrNull(),
        whenCookingCompleted = order?.whenCookingCompleted?.toHumanDateTimeOrNull(),
        whenCreated = order?.whenCreated?.toHumanDateTimeOrNull(),
        whenDelivered = order?.whenDelivered?.toHumanDateTimeOrNull(),
        whenPacked = order?.whenPacked?.toHumanDateTimeOrNull(),
        whenPrinted = order?.whenPrinted?.toHumanDateTimeOrNull(),
        whenSent = order?.whenSended?.toHumanDateTimeOrNull(),
        problem = order?.problem,

        )
}

fun IncomingOrderItemDto.toDomain() = IncomingOrderItem(
    id = product.id,
    name = product.name.applyTypography(),
    amount = amount,
    chosenModifiers = modifiers?.map { it.toDomain() } ?: emptyList(),
    price = price,
    positionId = positionId,
    deleted = deleted?.toDomain() ?: DeletionInfo(),
    comment = comment ?: ""
)

private fun DeliveryPointDto.toAddress(): Address {
    val point = coordinates?.let {
        GeoPoint(
            latitude = it.latitude,
            longitude = it.longitude
        )
    }
    return Address(
        point = point,
        streetAndBuilding = address?.line1 ?: "",
        apartmentNumber = address?.flat ?: "",
        entrance = address?.entrance ?: "",
        floor = address?.floor ?: "",
        intercom = address?.doorphone ?: "",
        comment = comment ?: ""
    )
}

private fun IncomingModifierDto.toDomain() = IncomingModifier(
    id = product.id,
    name = product.name.applyTypography(),
    amount = amount,
    price = price,
    modifierGroupId = productGroup.id,
    modifierGroupName = productGroup.name
)

private fun DeletionInfoDto.toDomain() = DeletionInfo(
    isDeleted = deletionMethod != null,
    comment = deletionMethod?.comment,
    removalType = deletionMethod?.removalType?.name
)

private fun String.toDeliveryStatus(): DeliveryStatus {
    return DeliveryStatus.entries.find { it.apiName.equals(this, ignoreCase = true) }
        ?: DeliveryStatus.UNCONFIRMED
}
