package com.mandarinkafe.mandarin.features.order.data.mapper

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.MealAdditional
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DISCOUNT_APPLIED
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DISCOUNT_PERCENT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_TECH_PART
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_USER_COMMENT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.PRICE_DECIMALS
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.UTENSILS_NEED_PREFIX
import com.mandarinkafe.mandarin.features.order.data.network.dto.ErrorInfoDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.LoyaltyCustomerResponse
import com.mandarinkafe.mandarin.features.order.data.network.dto.OrderInfoDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.AddressDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.Customer
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.DeliveryPoint
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.Item
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.OrderDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.OrderModifier
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.Payment
import com.mandarinkafe.mandarin.features.order.data.network.dto.createdelivery.Street
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
        userComment = comment,
        noNeedUtensils = utensils.noNeedUtensils,
        chosenUtensils = utensils.chosenUtensils,
        noChange = if (cash) paymentInfo.noChange else null,
        changeFrom = if (cash) paymentInfo.changeFrom else "",
        discountCategory = cartSummary.discountCategory
    )

    return Order(
        name = userInfo.name,
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
        customer = Customer(
            name = name,
            type = OrderConstants.CUSTOMER_TYPE_ONE_TIME
        ),
        items = cartItems.flatMap { (customizedMeal, quantity) ->
            val mealItem = customizedMeal.toItem(quantity, discountCategory)
            val addsItems = customizedMeal.adds.map { it.toItem(quantity, discountCategory) }
            listOf(mealItem) + addsItems
        },
        payments = listOf(
            Payment(
                paymentTypeKind = paymentType.paymentTypeKind,
                paymentTypeId = paymentType.id,
                sum = totalOrderSum,
            )
        )
    )
}

fun CustomizedMeal.toItem(quantity: Int, discountSize: Int): Item {
    val discountMultiplier =
        (OrderConstants.FULL_PERCENT - discountSize) / OrderConstants.FULL_PERCENT_DOUBLE

    val discountedPrice = if (meal.discountable) {
        (meal.price.toDouble() * discountMultiplier).roundTo(PRICE_DECIMALS)
    } else {
        meal.price.toDouble()
    }

    val discountedModifiers = modifiers.flatMap { group ->
        group.items.map {
            OrderModifier(
                id = it.id,
                amount = 1.0,
                price = (it.price.toDouble() * discountMultiplier).roundTo(PRICE_DECIMALS)
            )
        }
    }

    return Item(
        productId = meal.id,
        modifiers = discountedModifiers,
        price = discountedPrice,
        amount = quantity.toDouble(),
        type = meal.orderItemType,
    )
}

fun MealAdditional.toItem(quantity: Int = 1, discountSize: Int): Item {
    val discountMultiplier =
        (OrderConstants.FULL_PERCENT - discountSize) / OrderConstants.FULL_PERCENT_DOUBLE

    val discountedPrice = if (discountable) {
        (price.toDouble() * discountMultiplier).roundTo(PRICE_DECIMALS)
    } else {
        price.toDouble()
    }

    return Item(
        productId = id,
        modifiers = emptyList(),
        price = discountedPrice,
        amount = quantity.toDouble(),
        type = orderItemType
    )
}

fun Address?.toDeliveryPoint(): DeliveryPoint? {
    return if (this == null) {
        null
    } else {
        DeliveryPoint(
            address = AddressDto(
                street = Street(name = this.streetAndBuilding, id = null),
                flat = apartmentNumber,
                entrance = entrance,
                floor = floor,
                doorphone = intercom,
                type = OrderConstants.ADDRESS_TYPE_LEGACY
            ),
            comment = comment.ifBlank { null }
        )
    }
}

fun buildFullComment(
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
