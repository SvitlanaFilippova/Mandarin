package com.mandarinkafe.mandarin.features.order.data.mapper

import com.mandarinkafe.mandarin.core.data.dto.order.AddressDto
import com.mandarinkafe.mandarin.core.data.dto.order.Coordinates
import com.mandarinkafe.mandarin.core.data.dto.order.CustomerDto
import com.mandarinkafe.mandarin.core.data.dto.order.DeliveryPointDto
import com.mandarinkafe.mandarin.core.data.dto.order.StreetDto
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DEFAULT_AMOUNT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DISCOUNT_APPLIED
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DISCOUNT_PERCENT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_TECH_PART
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_USER_COMMENT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.PRICE_DECIMALS
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.UTENSILS_NEED_PREFIX
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingOrderDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingPaymentDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.loyalty.LoyaltyCustomerResponse
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.LoyaltyCustomer
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingModifier
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrderItem
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.Utensils
import com.mandarinkafe.mandarin.util.roundTo

fun LoyaltyCustomerResponse.toDomain(): LoyaltyCustomer {
    return LoyaltyCustomer(
        id = id,
        isDeleted = isDeleted == true,
        maxDiscountPercent = maxDiscount
    )
}

fun OrderState.toDomain(paymentType: PaymentType): OutgoingOrder {
    val cash = paymentType.code == OrderConstants.PAYMENT_CASH_CODE
    val fullComment = buildFullComment(
        userComment = comment.trim(),
        utensils = utensils,
        noChange = if (cash) paymentInfo.noChange else null,
        changeFrom = if (cash) paymentInfo.changeFrom else "",
        discountCategory = cartSummary.discountCategory
    )

    return OutgoingOrder(
        name = userInfo.name.trim(),
        phone = userInfo.phone,
        deliveryType = deliveryInfo.deliveryType
            ?: error("deliveryType is null"),
        chosenAddress = deliveryInfo.chosenAddress,
        paymentType = paymentType,
        comment = fullComment,
        items = cartSummary.items.toOrderItemsRequest(discountCategory = cartSummary.discountCategory),
        deliveryRealCost = deliveryCost,
        totalOrderSum = totalOrderSum,
        discountCategory = cartSummary.discountCategory,
        deliveryZoneID = deliveryInfo.deliveryZone?.id
    )
}

fun OutgoingOrder.toOrderDto(): OutgoingOrderDto {
    return OutgoingOrderDto(
        phone = OrderConstants.PHONE_PREFIX + phone,
        orderServiceType = if (deliveryType == DeliveryType.DELIVERY) {
            OrderConstants.DELIVERY_TYPE_DELIVERY
        } else {
            OrderConstants.DELIVERY_TYPE_PICKUP
        },
        deliveryPoint = chosenAddress?.toDeliveryPointDto(),
        comment = comment,
        customer = CustomerDto(
            name = name,
            type = OrderConstants.CUSTOMER_TYPE_ONE_TIME
        ),
        items = items,
        payments = listOf(
            OutgoingPaymentDto(
                paymentTypeKind = paymentType.paymentTypeKind,
                paymentTypeId = paymentType.id,
                sum = totalOrderSum,
            )
        )
    )
}

fun List<CartItem>.toOrderItemsRequest(discountCategory: Int): List<OutgoingOrderItem> {
    return this.flatMap { (customizedMeal, quantity) ->
        val mealItem = customizedMeal.toOutgoingOrderItem(quantity, discountCategory)
        val addsItems =
            customizedMeal.adds.map { it.toOutgoingOrderItem(quantity, discountCategory) }
        listOf(mealItem) + addsItems
    }
}

fun CustomizedMeal.toOutgoingOrderItem(quantity: Int, discountSize: Int): OutgoingOrderItem {
    val discountMultiplier =
        (OrderConstants.FULL_PERCENT - discountSize) / OrderConstants.FULL_PERCENT_DOUBLE

    val discountedPrice = if (meal.discountable) {
        (meal.price.toDouble() * discountMultiplier).roundTo(PRICE_DECIMALS)
    } else {
        meal.price.toDouble()
    }
    val discountedModifiers =
        modifiers.flatMap { group -> group.toOutgoingModifier(discountMultiplier) }

    return OutgoingOrderItem(
        productId = meal.id,
        modifiers = discountedModifiers,
        price = discountedPrice,
        amount = quantity.toDouble(),
        type = meal.orderItemType,
    )
}

fun MealAdditional.toOutgoingOrderItem(quantity: Int = 1, discountSize: Int): OutgoingOrderItem {
    val discountMultiplier =
        (OrderConstants.FULL_PERCENT - discountSize) / OrderConstants.FULL_PERCENT_DOUBLE

    val discountedPrice = if (discountable) {
        (price.toDouble() * discountMultiplier).roundTo(PRICE_DECIMALS)
    } else {
        price.toDouble()
    }

    return OutgoingOrderItem(
        productId = id,
        price = discountedPrice,
        amount = quantity.toDouble(),
        type = orderItemType
    )
}

fun ModifierGroup.toOutgoingModifier(discountMultiplier: Double): List<OutgoingModifier> {
    return items.map {
        OutgoingModifier(
            productId = it.id,
            amount = DEFAULT_AMOUNT,
            price = it.price * discountMultiplier,
            productGroupId = this.id
        )
    }
}

fun Meal.toOutgoingOrderItem() = OutgoingOrderItem(
    productId = id,
    price = price.toDouble(),
    amount = 1.0,
    type = orderItemType,
)

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
    discountCategory: Int
): String {
    val utensilsPart = when {
        utensils.noNeedUtensils -> OrderConstants.NO_UTENSILS_COMMENT
        utensils.chosenUtensils.isNotEmpty() -> UTENSILS_NEED_PREFIX +
                utensils.chosenUtensils.joinToString { it.stringName }
        else -> null
    }

    val changePart = when {
        noChange == null -> null
        noChange -> OrderConstants.NO_CHANGE_COMMENT
        changeFrom.isNotEmpty() -> OrderConstants.CHANGE_FROM_COMMENT_PREFIX + changeFrom
        else -> null
    }

    val techParts = listOfNotNull(utensilsPart, changePart)
        .takeIf { it.isNotEmpty() }
        ?.joinToString()

    val discountPart = if (discountCategory != 0) {
        DISCOUNT_APPLIED + discountCategory + DISCOUNT_PERCENT
    } else {
        null
    }

    val fullTechnicalComment = listOfNotNull(techParts, discountPart)
        .takeIf { it.isNotEmpty() }
        ?.joinToString(DIVIDER_FOR_TECH_PART)

    return buildString {
        append(userComment.trim())

        if (userComment.isNotBlank() && !fullTechnicalComment.isNullOrBlank()) {
            append(DIVIDER_FOR_USER_COMMENT)
        }

        if (!fullTechnicalComment.isNullOrBlank()) {
            append(fullTechnicalComment)
        }
    }.trim()
}

