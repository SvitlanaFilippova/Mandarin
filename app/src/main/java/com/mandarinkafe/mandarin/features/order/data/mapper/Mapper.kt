package com.mandarinkafe.mandarin.features.order.data.mapper

import com.mandarinkafe.mandarin.core.data.dto.order.AddressDto
import com.mandarinkafe.mandarin.core.data.dto.order.Coordinates
import com.mandarinkafe.mandarin.core.data.dto.order.CustomerDto
import com.mandarinkafe.mandarin.core.data.dto.order.DeliveryPointDto
import com.mandarinkafe.mandarin.core.data.dto.order.ItemDto
import com.mandarinkafe.mandarin.core.data.dto.order.OrderDto
import com.mandarinkafe.mandarin.core.data.dto.order.OrderInfoDto
import com.mandarinkafe.mandarin.core.data.dto.order.OrderModifierDto
import com.mandarinkafe.mandarin.core.data.dto.order.PaymentDto
import com.mandarinkafe.mandarin.core.data.dto.order.StreetDto
import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DEFAULT_AMOUNT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DELIVERY_ID
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DELIVERY_ITEM_TYPE
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DISCOUNT_APPLIED
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DISCOUNT_PERCENT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_TECH_PART
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_USER_COMMENT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.PRICE_DECIMALS
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.UTENSILS_NEED_PREFIX
import com.mandarinkafe.mandarin.features.order.data.network.dto.ErrorInfoDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.loyalty.LoyaltyCustomerResponse
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.ErrorInfo
import com.mandarinkafe.mandarin.features.order.domain.models.LoyaltyCustomer
import com.mandarinkafe.mandarin.features.order.domain.models.Order
import com.mandarinkafe.mandarin.features.order.domain.models.OrderInfo
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.util.roundTo

fun LoyaltyCustomerResponse.toDomain(): LoyaltyCustomer {
    return LoyaltyCustomer(
        id = id,
        isDeleted = isDeleted == true,
        maxDiscountPercent = maxDiscount
    )
}

fun OrderState.toDomain(paymentType: PaymentType): Order {
    val cash = paymentType.code == OrderConstants.PAYMENT_CASH_CODE
    val fullComment = buildFullComment(
        userComment = comment.trim(),
        noNeedUtensils = utensils.noNeedUtensils,
        chosenUtensils = utensils.chosenUtensils,
        noChange = if (cash) paymentInfo.noChange else null,
        changeFrom = if (cash) paymentInfo.changeFrom else "",
        discountCategory = cartSummary.discountCategory
    )

    return Order(
        name = userInfo.name.trim(),
        phone = userInfo.phone,
        deliveryType = deliveryInfo.deliveryType
            ?: error("deliveryType is null"),
        chosenAddress = deliveryInfo.chosenAddress,
        paymentType = paymentType,
        comment = fullComment,
        cartItems = cartSummary.items,
        deliveryRealCost = deliveryCost,
        totalOrderSum = totalOrderSum,
        discountCategory = cartSummary.discountCategory
    )
}

fun Order.toOrderDto(): OrderDto {
    return OrderDto(
        phone = OrderConstants.PHONE_PREFIX + phone,
        orderServiceType = if (deliveryType == DeliveryType.DELIVERY) {
            OrderConstants.DELIVERY_TYPE_DELIVERY
        } else {
            OrderConstants.DELIVERY_TYPE_PICKUP
        },
        deliveryPoint = chosenAddress?.toDeliveryPoint(),
        comment = comment,
        customer = CustomerDto(
            name = name,
            type = OrderConstants.CUSTOMER_TYPE_ONE_TIME
        ),
        items = cartItems.flatMap { (customizedMeal, quantity) ->
            val mealItem = customizedMeal.toItem(quantity, discountCategory)
            val addsItems = customizedMeal.adds.map { it.toItem(quantity, discountCategory) }
            listOf(mealItem) + addsItems + createDelivery(deliveryRealCost)
        },
        payments = listOf(
            PaymentDto(
                paymentTypeKind = paymentType.paymentTypeKind,
                paymentTypeId = paymentType.id,
                sum = totalOrderSum,
            )
        )
    )
}

private fun createDelivery(price: Int): ItemDto {
    return ItemDto(
        productId = DELIVERY_ID,
        price = price.toDouble(),
        amount = DEFAULT_AMOUNT,
        type = DELIVERY_ITEM_TYPE
    )
}

fun CustomizedMeal.toItem(quantity: Int, discountSize: Int): ItemDto {
    val discountMultiplier =
        (OrderConstants.FULL_PERCENT - discountSize) / OrderConstants.FULL_PERCENT_DOUBLE

    val discountedPrice = if (meal.discountable) {
        (meal.price.toDouble() * discountMultiplier).roundTo(PRICE_DECIMALS)
    } else {
        meal.price.toDouble()
    }
    val discountedModifiers =
        modifiers.flatMap { group -> group.toOrderModifierDto(discountMultiplier) }

    return ItemDto(
        productId = meal.id,
        modifiers = discountedModifiers,
        price = discountedPrice,
        amount = quantity.toDouble(),
        type = meal.orderItemType,
    )
}

fun ModifierGroup.toOrderModifierDto(discountMultiplier: Double): List<OrderModifierDto> {
    return items.map {
        OrderModifierDto(
            productId = it.id,
            amount = DEFAULT_AMOUNT,
            price = it.price * discountMultiplier,
            productGroupId = this.id
        )
    }
}

fun MealAdditional.toItem(quantity: Int = 1, discountSize: Int): ItemDto {
    val discountMultiplier =
        (OrderConstants.FULL_PERCENT - discountSize) / OrderConstants.FULL_PERCENT_DOUBLE

    val discountedPrice = if (discountable) {
        (price.toDouble() * discountMultiplier).roundTo(PRICE_DECIMALS)
    } else {
        price.toDouble()
    }

    return ItemDto(
        productId = id,
        price = discountedPrice,
        amount = quantity.toDouble(),
        type = orderItemType
    )
}

fun Address?.toDeliveryPoint(): DeliveryPointDto? {
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
    noNeedUtensils: Boolean,
    chosenUtensils: List<Utensil>,
    noChange: Boolean?,
    changeFrom: String,
    discountCategory: Int
): String {
    val utensilsPart = when {
        noNeedUtensils -> OrderConstants.NO_UTENSILS_COMMENT
        chosenUtensils.isNotEmpty() -> UTENSILS_NEED_PREFIX +
                chosenUtensils.joinToString { it.stringName }

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

fun OrderInfoDto.toDomain() = OrderInfo(
    id = id,
    timestamp = timestamp,
    creationStatus = creationStatus,
    errorInfo = errorInfo?.toDomain()
)

fun ErrorInfoDto.toDomain() = ErrorInfo(
    code = code,
    message = message,
    errorReason = errorReason,
    additionalData = additionalData
)
