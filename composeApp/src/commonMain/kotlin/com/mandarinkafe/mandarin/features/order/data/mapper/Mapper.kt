package com.mandarinkafe.mandarin.features.order.data.mapper

import com.mandarinkafe.mandarin.core.data.dto.order.AddressDto
import com.mandarinkafe.mandarin.core.data.dto.order.Coordinates
import com.mandarinkafe.mandarin.core.data.dto.order.DeliveryPointDto
import com.mandarinkafe.mandarin.core.data.dto.order.StreetDto
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DEFAULT_AMOUNT
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingModifier
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrderItem
import com.mandarinkafe.mandarin.util.COMMENT_TECH_VISIBILITY_DIVIDER
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun List<CartItem>.toOrderItems(): List<OutgoingOrderItem> {
    val items = this.flatMap { item ->
        val modifiersWithPosIds = item.customizedMeal.modifiers.map {
            it.toOutgoingModifier()
        }.flatten()

        val mealItem = item.customizedMeal.toOutgoingOrderItem(
            quantity = item.quantity,
            comment = item.comment,
        ).copy(modifiers = modifiersWithPosIds)

        val addsItems = item.customizedMeal.adds.map { add ->
            add.toOutgoingOrderItem(item.quantity)
        }

        listOf(mealItem) + addsItems
    }
    return items
}

@OptIn(ExperimentalUuidApi::class)
fun CustomizedMeal.toOutgoingOrderItem(
    quantity: Int,
    comment: String,
): OutgoingOrderItem {
    return OutgoingOrderItem(
        productId = meal.id,
        price = meal.price.toDouble(),
        amount = quantity.toDouble(),
        type = meal.orderItemType,
        comment = comment,
        positionId = Uuid.random().toString(),
        discountable = meal.discountable
    )
}

@OptIn(ExperimentalUuidApi::class)
fun MealAdditional.toOutgoingOrderItem(
    quantity: Int,
): OutgoingOrderItem {
    return OutgoingOrderItem(
        productId = id,
        price = price.toDouble(),
        amount = quantity.toDouble(),
        type = orderItemType,
        positionId = Uuid.random().toString(),
    )
}

@OptIn(ExperimentalUuidApi::class)
fun ModifierGroup.toOutgoingModifier(): List<OutgoingModifier> {
    return items.map {
        OutgoingModifier(
            productId = it.id,
            amount = DEFAULT_AMOUNT,
            price = it.price.toDouble(),
            productGroupId = this.id,
            positionId = Uuid.random().toString(),
        )
    }
}

fun Address?.toDeliveryPointDto(internalPaymentSuffix: String? = null): DeliveryPointDto? {
    return if (this == null) {
        null
    } else {
        val coordinates = this.point?.let {
            Coordinates(
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
        DeliveryPointDto(
            coordinates = coordinates,
            address = AddressDto(
                street = StreetDto(name = this.streetAndBuilding),
                house = "",
                flat = apartmentNumber,
                entrance = entrance,
                floor = floor,
                doorphone = intercom,
                type = OrderConstants.ADDRESS_TYPE_LEGACY,
            ),
            comment = buildDeliveryPointCourierComment(comment, internalPaymentSuffix),
        )
    }
}

private fun buildDeliveryPointCourierComment(
    addressComment: String,
    internalPaymentSuffix: String?,
): String? {
    val base = addressComment.trim()
    val suffix = internalPaymentSuffix?.trim().orEmpty()
    if (base.isEmpty() && suffix.isEmpty()) return null
    if (suffix.isEmpty()) return base.ifEmpty { null }
    return if (base.isEmpty()) {
        COMMENT_TECH_VISIBILITY_DIVIDER + suffix
    } else {
        base + COMMENT_TECH_VISIBILITY_DIVIDER + suffix
    }
}






