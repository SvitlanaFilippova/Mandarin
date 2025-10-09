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
import com.mandarinkafe.mandarin.features.infrastructure.data.network.LoyaltyCustomerResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.discounts.CustomerCategoryDto
import com.mandarinkafe.mandarin.features.infrastructure.domain.models.CustomerCategory
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DEFAULT_AMOUNT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_TECH_PART
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_USER_COMMENT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.UTENSILS_NEED_PREFIX
import com.mandarinkafe.mandarin.features.order.domain.models.LoyaltyCustomer
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingModifier
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrderItem
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.Utensils
import java.util.UUID

fun LoyaltyCustomerResponse.toDomain() = LoyaltyCustomer(
    id = id ?: "",
    isDeleted = isDeleted == true,
    categories = categories?.map { it.toDomain() } ?: emptyList(),
)

fun CustomerCategoryDto.toDomain() = CustomerCategory(
    id = id ?: "",
    name = name ?: "",
    discountPercent = name?.toIntOrNull(),
    isActive = isActive == true
)

fun OrderState.toDomain(paymentType: PaymentType): OutgoingOrder {
    val cash = paymentType.code == OrderConstants.PAYMENT_CASH_CODE
    val fullComment = buildFullComment(
        userComment = comment.trim(),
        utensils = utensils,
        rawPaymentType = paymentInfo.chosenPaymentTypeDomain.paymentTypeKind,
        noChange = if (cash) paymentInfo.noChange else null,
        changeFrom = if (cash) paymentInfo.changeFrom else "",
    )
    val address = if (deliveryInfo.isPickup) null else deliveryInfo.chosenAddress
    return OutgoingOrder(
        name = userInfo.name.trim(),
        phone = userInfo.phone,
        deliveryType = deliveryInfo.deliveryType
            ?: error("deliveryType is null"),
        chosenAddress = address,
        paymentType = paymentType,
        comment = fullComment,
        items = cartSummary.items,
        deliveryRealCost = deliveryCost,
        totalOrderSum = totalOrderSum,
        discountPercent = cartSummary.discountPercent,
        discountTypeId = cartSummary.discountId,
        deliveryZoneID = deliveryInfo.deliveryZone?.id,
    )
}

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
        positionId = UUID.randomUUID().toString(),
        discountable = meal.discountable
    )
}

fun MealAdditional.toOutgoingOrderItem(
    quantity: Int,
): OutgoingOrderItem {
    return OutgoingOrderItem(
        productId = id,
        price = price.toDouble(),
        amount = quantity.toDouble(),
        type = orderItemType,
        positionId = UUID.randomUUID().toString(),

        )
}

fun ModifierGroup.toOutgoingModifier(): List<OutgoingModifier> {
    return items.map {
        OutgoingModifier(
            productId = it.id,
            amount = DEFAULT_AMOUNT,
            price = it.price.toDouble(),
            productGroupId = this.id,
            positionId = UUID.randomUUID().toString(),
        )
    }
}

fun Address?.toDeliveryPointDto(): DeliveryPointDto? {
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
            comment = comment.ifBlank { null }
        )
    }
}

private fun buildFullComment(
    userComment: String,
    utensils: Utensils,
    noChange: Boolean?,
    changeFrom: String,
    rawPaymentType: String,
): String {
    val utensilsPart = when {
        utensils.noNeedUtensils -> OrderConstants.NO_UTENSILS_COMMENT
        utensils.chosenUtensils.isNotEmpty() -> UTENSILS_NEED_PREFIX +
                utensils.chosenUtensils.joinToString { it.stringName }

        else -> null
    }

    val paymentType = when (rawPaymentType.lowercase()) {
        OrderConstants.PAYMENT_CASH_NAME -> "Наличными"
        OrderConstants.PAYMENT_CARD_NAME -> "Картой при получении"
        else -> rawPaymentType
    }

    val paymentTypePart = OrderConstants.PAYMENT_TYPE_COMMENT_PREFIX + paymentType
    val changePart = when {
        noChange == null -> null
        noChange -> OrderConstants.NO_CHANGE_COMMENT
        changeFrom.isNotEmpty() -> OrderConstants.CHANGE_FROM_COMMENT_PREFIX + changeFrom
        else -> null
    }

    val techParts = listOfNotNull(paymentTypePart, changePart, utensilsPart)
        .takeIf { it.isNotEmpty() }
        ?.joinToString(DIVIDER_FOR_TECH_PART)


    return buildString {
        append(userComment.trim())

        if (userComment.isNotBlank() && !techParts.isNullOrBlank()) {
            append(DIVIDER_FOR_USER_COMMENT)
        }

        if (!techParts.isNullOrBlank()) {
            append(techParts)
        }
    }.trim()
}

