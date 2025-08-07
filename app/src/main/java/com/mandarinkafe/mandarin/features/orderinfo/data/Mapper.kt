package com.mandarinkafe.mandarin.features.orderinfo.data

import com.mandarinkafe.mandarin.core.data.dto.order.DeliveryPointDto
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.DeletionInfoDto
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.IncomingModifierDto
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.IncomingOrderItemDto
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.OrderInfoResponseDto
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeletionInfo
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingMealAdditional
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingModifier
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingOrderItem
import com.mandarinkafe.mandarin.util.DateTimeUtils.toHumanDateTimeOrNull
import com.mandarinkafe.mandarin.util.applyTypography

fun OrderInfoResponseDto.toDomain(addons: List<MealAdditionalCategory>): IncomingOrder {
    val cancelInfo = buildString {
        val cause = order?.cancelInfo?.cause?.name.orEmpty().applyTypography()
        val comment = order?.cancelInfo?.comment.orEmpty().applyTypography()

        if (cause.isNotBlank()) append(cause)
        if (comment.isNotBlank()) {
            if (isNotEmpty()) append(": ")
            append(comment)
        }
    }

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
        items = order?.items?.toDomainWithAdds(addons) ?: emptyList(),
        paymentName = order?.payments?.firstOrNull()?.paymentType?.name,
        status = order?.status?.toDeliveryStatus() ?: DeliveryStatus.UNCONFIRMED,
        cancelInfo = cancelInfo,
        orderType = order?.orderType,
        processedPaymentsSum = order?.processedPaymentsSum,
        sum = order?.sum,
        whenCancelled = order?.cancelInfo?.whenCancelled?.toHumanDateTimeOrNull(),
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

fun List<IncomingOrderItemDto>.toDomainWithAdds(
    addonsCategories: List<MealAdditionalCategory>
): List<IncomingOrderItem> {
    val addonIds = collectAddonIds(addonsCategories)
    val builders = associateItemsWithAdds(this, addonIds)
    return builders.map { it.build() }
}

private fun collectAddonIds(addonsCategories: List<MealAdditionalCategory>): Set<String> =
    addonsCategories.flatMap { it.items.map { add -> add.id } }.toSet()

private fun associateItemsWithAdds(
    dtos: List<IncomingOrderItemDto>,
    addonIds: Set<String>
): List<IncomingOrderItemBuilder> {
    val result = mutableListOf<IncomingOrderItemBuilder>()

    for (dto in dtos) {
        val isAddon = dto.product.id in addonIds

        if (!isAddon) {
            result += IncomingOrderItemBuilder.fromDto(dto)
        } else {
            val addon = IncomingMealAdditional(
                id = dto.product.id,
                name = dto.product.name,
                amount = dto.amount,
                price = dto.price
            )
            if (result.isNotEmpty()) {
                result.last().chosenAdds += addon
            } else {
                result += IncomingOrderItemBuilder.placeholderWithAddon(addon)
            }
        }
    }

    return result
}

private data class IncomingOrderItemBuilder(
    val id: String,
    val name: String,
    val amount: Double,
    val price: Double,
    val positionId: String?,
    val deleted: DeletionInfo,
    val comment: String,
    var chosenModifiers: List<IncomingModifier> = emptyList(),
    val chosenAdds: MutableList<IncomingMealAdditional> = mutableListOf()
) {
    fun build() = IncomingOrderItem(
        id = id,
        name = name,
        amount = amount,
        chosenModifiers = chosenModifiers,
        chosenAdds = chosenAdds.toList(),
        price = price,
        positionId = positionId,
        deleted = deleted,
        comment = comment
    )

    companion object {
        fun fromDto(dto: IncomingOrderItemDto): IncomingOrderItemBuilder {
            return IncomingOrderItemBuilder(
                id = dto.product.id,
                name = dto.product.name,
                amount = dto.amount,
                price = dto.price,
                positionId = dto.positionId,
                deleted = dto.deleted?.toDomain() ?: DeletionInfo(),
                comment = dto.comment ?: "",
                chosenModifiers = dto.modifiers?.map { it.toDomain() } ?: emptyList()
            )
        }

        fun placeholderWithAddon(addon: IncomingMealAdditional): IncomingOrderItemBuilder {
            return IncomingOrderItemBuilder(
                id = "unknown",
                name = "Неизвестное блюдо",
                amount = 0.0,
                price = 0.0,
                positionId = null,
                deleted = DeletionInfo(),
                comment = ""
            ).apply { chosenAdds += addon }
        }
    }
}