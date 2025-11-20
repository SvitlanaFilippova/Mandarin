package com.mandarinkafe.mandarin.features.orderinfo.data

import com.mandarinkafe.mandarin.core.data.dto.order.DeliveryPointDto
import com.mandarinkafe.mandarin.core.data.dto.order.OrderTypeDto
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.GeoPoint
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.core.domain.models.MeasureUnitType
import com.mandarinkafe.mandarin.core.domain.models.OrderType
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants
import com.mandarinkafe.mandarin.features.order.data.network.dto.ErrorInfoDto
import com.mandarinkafe.mandarin.features.order.domain.models.CreationStatus
import com.mandarinkafe.mandarin.features.order.domain.models.ErrorInfo
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.IncomingModifierDto
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.IncomingOrderItemDto
import com.mandarinkafe.mandarin.features.orderinfo.data.network.dto.OrderInfoResponseDto
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingMealAdditional
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingModifier
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingOrderItem
import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.util.DateTimeUtils.toHumanDateTimeOrNull
import com.mandarinkafe.mandarin.util.applyTypography
import com.mandarinkafe.mandarin.util.toVisibleComment

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
    val orderType = order?.orderType?.toDomain()

    return IncomingOrder(
        id = id,
        number = order?.number,
        timestamp = timestamp,
        creationStatus = creationStatus?.let { CreationStatus.fromApiName(it) }
            ?: CreationStatus.IN_PROGRESS,
        errorInfo = errorInfo?.toDomain(),
        phone = order?.phone,
        deliveryAddress = order?.deliveryPoint?.toAddress(),
        comment = order?.comment?.toVisibleComment(),
        customerName = order?.customer?.name,
        items = order?.items?.toDomainWithAdds(addons) ?: emptyList(),
        paymentName = order?.payments?.firstOrNull()?.paymentType?.name,
        paymentMethodCode = order?.paymentMethodCode,
        status = order?.status?.toDeliveryStatus() ?: DeliveryStatus.UNCONFIRMED,
        cancelInfo = cancelInfo,
        orderType = orderType,
        processedPaymentsSum = order?.processedPaymentsSum,
        sum = order?.sum,
        discountReason = order?.discounts?.firstOrNull()?.discountType?.name?.applyTypography(),
        whenCancelled = order?.cancelInfo?.whenCancelled?.toHumanDateTimeOrNull(),
        whenClosed = order?.whenClosed?.toHumanDateTimeOrNull(),
        whenConfirmed = order?.whenConfirmed?.toHumanDateTimeOrNull(),
        whenCookingCompleted = order?.whenCookingCompleted?.toHumanDateTimeOrNull(),
        whenCreated = order?.whenCreated?.toHumanDateTimeOrNull(),
        whenDelivered = order?.whenDelivered?.toHumanDateTimeOrNull(),
        whenSent = order?.whenSended?.toHumanDateTimeOrNull(),
        isDelivery = orderType?.orderServiceType == OrderConstants.DELIVERY_TYPE_DELIVERY
    )
}

fun OrderTypeDto.toDomain() = OrderType(
    id = id,
    name = name,
    orderServiceType = orderServiceType,
)

fun IncomingOrderItemDto.toDomain(): IncomingOrderItem {
    val baseUrl = BuildKonfig.SERVER_BASE_URL.removeSuffix("/")
    val imageUrl = "$baseUrl/images_previews/${product.id}.jpg"
    val blurredPreviewUrl = "$baseUrl/images_previews/${product.id}_placeholder.jpg"

    val safeAmount = amount ?: 1.0
    return IncomingOrderItem(
        id = product.id,
        name = product.name.applyTypography(),
        amount = safeAmount,
        chosenModifiers = modifiers.map { it.toDomain(safeAmount) },
        price = price,
        positionId = positionId,
        isDeleted = deleted?.deletionMethod != null,
        comment = comment ?: "",
        discountedPrice = resultSum?.takeIf { it > 0 }
            ?.div(safeAmount), // делим на количество, чтобы узнать цену за единицу
        imageUrl = imageUrl,
        blurredPreviewUrl = blurredPreviewUrl,
    )
}

fun List<IncomingOrderItemDto>.toDomainWithAdds(
    addonsCategories: List<MealAdditionalCategory>,
): List<IncomingOrderItem> {
    val addonIds = collectAddonIds(addonsCategories)
    val builders = associateItemsWithAdds(this, addonIds)
    return builders.map { it.build() }
}

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
        comment = comment.toVisibleComment()
    )
}

private fun IncomingModifierDto.toDomain(mealAmount: Double): IncomingModifier {
    val safeAmount = amount ?: 1.0
    // Если product или productGroup отсутствуют, используем пустые значения
    // Они будут обновлены позже через updateNamesFrom в OrderInfoRepositoryImpl
    val modifierId = product?.id ?: ""
    val modifierName = product?.name?.applyTypography() ?: ""
    val modifierGroupId = productGroup?.id ?: ""
    val modifierGroupName = productGroup?.name ?: ""

    return IncomingModifier(
        id = modifierId,
        name = modifierName,
        amount = safeAmount,
        price = price,
        groupId = modifierGroupId,
        groupName = modifierGroupName,
        discountedPrice = resultSum?.takeIf { it > 0 }?.div(mealAmount * safeAmount)
    )
}

private fun String.toDeliveryStatus(): DeliveryStatus {
    return DeliveryStatus.entries.find { it.apiName.equals(this, ignoreCase = true) }
        ?: DeliveryStatus.UNCONFIRMED
}

fun ErrorInfoDto.toDomain() = ErrorInfo(
    code = code,
    message = message,
    errorReason = errorReason,
)

private fun collectAddonIds(addonsCategories: List<MealAdditionalCategory>): Set<String> =
    addonsCategories.flatMap { it.items.map { add -> add.id } }.toSet()

private fun associateItemsWithAdds(
    dtos: List<IncomingOrderItemDto>,
    addonIds: Set<String>,
): List<IncomingOrderItemBuilder> {
    val result = mutableListOf<IncomingOrderItemBuilder>()

    for (dto in dtos) {
        val isAddon = dto.product.id in addonIds

        if (!isAddon) {
            result += IncomingOrderItemBuilder.fromDto(dto)
        } else {
            val safeAmount = dto.amount ?: 1.0
            val addon = IncomingMealAdditional(
                id = dto.product.id,
                name = dto.product.name.applyTypography(),
                amount = safeAmount,
                price = dto.price,
                discountedPrice = dto.resultSum?.takeIf { it > 0 }?.div(safeAmount),
                weight = dto.weight ?: 0,
                measureUnitType = MeasureUnitType.from(dto.measureUnitType) ?: MeasureUnitType.GRAM,
                isDeleted = dto.deleted?.deletionMethod != null,
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

private class IncomingOrderItemBuilder private constructor(
    private val id: String,
    private val name: String,
    private val amount: Double,
    private val price: Double,
    private val discountedPrice: Double?,
    private val positionId: String?,
    private val isDeleted: Boolean,
    private val comment: String,
    private val imageUrl: String?,
    private val blurredPreviewUrl: String?,
    var chosenModifiers: List<IncomingModifier> = emptyList(),
    val chosenAdds: MutableList<IncomingMealAdditional> = mutableListOf(),
) {
    fun build() = IncomingOrderItem(
        id = id,
        name = name,
        amount = amount,
        chosenModifiers = chosenModifiers,
        chosenAdds = chosenAdds.toList(),
        price = price,
        positionId = positionId,
        isDeleted = isDeleted,
        comment = comment,
        discountedPrice = discountedPrice,
        imageUrl = imageUrl,
        blurredPreviewUrl = blurredPreviewUrl,
    )

    companion object {
        fun fromDto(dto: IncomingOrderItemDto): IncomingOrderItemBuilder {
            val safeAmount = dto.amount ?: 0.0
            val baseUrl = BuildKonfig.SERVER_BASE_URL.removeSuffix("/")
            val imageUrl = "$baseUrl/images_previews/${dto.product.id}.jpg"
            val blurredPreviewUrl = "$baseUrl/images_previews/${dto.product.id}_placeholder.jpg"

            return IncomingOrderItemBuilder(
                id = dto.product.id,
                name = dto.product.name.applyTypography(),
                amount = safeAmount,
                price = dto.price,
                positionId = dto.positionId,
                isDeleted = dto.deleted?.deletionMethod != null,
                comment = dto.comment.toVisibleComment(),
                chosenModifiers = dto.modifiers.map { it.toDomain(safeAmount) },
                discountedPrice = dto.resultSum?.takeIf { it > 0 }?.div(safeAmount),
                imageUrl = imageUrl,
                blurredPreviewUrl = blurredPreviewUrl,
            )
        }

        fun placeholderWithAddon(addon: IncomingMealAdditional): IncomingOrderItemBuilder {
            return IncomingOrderItemBuilder(
                id = "unknown",
                name = "Неизвестное блюдо",
                amount = 0.0,
                price = 0.0,
                positionId = null,
                isDeleted = false,
                comment = "",
                discountedPrice = null,
                imageUrl = null,
                blurredPreviewUrl = null
            ).apply { chosenAdds += addon }
        }
    }
}