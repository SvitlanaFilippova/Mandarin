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
import com.mandarinkafe.mandarin.features.discounts.data.network.LoyaltyCustomerResponse
import com.mandarinkafe.mandarin.features.discounts.data.network.dto.CustomerCategoryDto
import com.mandarinkafe.mandarin.features.discounts.domain.models.CustomerCategory
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DEFAULT_AMOUNT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_TECH_PART
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_USER_COMMENT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.UTENSILS_NEED_PREFIX
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingDiscountInfoDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingOrderDto
import com.mandarinkafe.mandarin.features.order.data.network.dto.OutgoingPaymentDto
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.domain.models.LoyaltyCustomer
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingModifier
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrderItem
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.Utensils

fun LoyaltyCustomerResponse.toDomain() = LoyaltyCustomer(
    id = id,
    isDeleted = isDeleted == true,
    categories = categories.map { it.toDomain() },
)

fun CustomerCategoryDto.toDomain() = CustomerCategory(
    id = id,
    name = name,
    discountPercent = name.toIntOrNull(),
    isActive = isActive
)

fun OrderState.toDomain(paymentType: PaymentType): OutgoingOrder {
    val cash = paymentType.code == OrderConstants.PAYMENT_CASH_CODE
    val fullComment = buildFullComment(
        userComment = comment.trim(),
        utensils = utensils,
        paymentType = paymentInfo.chosenPaymentTypeDomain.paymentTypeKind,
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

fun OutgoingOrder.toOrderDto(discountsInfo: OutgoingDiscountInfoDto?): OutgoingOrderDto {
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
        items = items.toOrderItemsRequest(),
        payments = listOf(
            OutgoingPaymentDto(
                paymentTypeKind = paymentType.paymentTypeKind,
                paymentTypeId = paymentType.id,
                sum = totalOrderSum,
                isPrepay = false,
                isProcessedExternally = false,
                isFiscalizedExternally = false,
            )
        ),
        discountsInfo = discountsInfo
    )
}

fun List<CartItem>.toOrderItemsRequest(): List<OutgoingOrderItem> {
    return this.flatMap { item ->
        val mealItem = item.customizedMeal.toOutgoingOrderItem(
            quantity = item.quantity,
            comment = item.comment
        )
        val addsItems =
            item.customizedMeal.adds.map { add ->
                add.toOutgoingOrderItem(item.quantity)
            }
        listOf(mealItem) + addsItems
    }
}

fun CustomizedMeal.toOutgoingOrderItem(
    quantity: Int,
    comment: String
): OutgoingOrderItem {
    return OutgoingOrderItem(
        productId = meal.id,
        modifiers = modifiers.flatMap { group -> group.toOutgoingModifier() },
        price = meal.price.toDouble(),
        amount = quantity.toDouble(),
        type = meal.orderItemType,
        comment = comment
    )
}

fun MealAdditional.toOutgoingOrderItem(quantity: Int = 1): OutgoingOrderItem {
    return OutgoingOrderItem(
        productId = id,
        price = price.toDouble(),
        amount = quantity.toDouble(),
        type = orderItemType
    )
}

fun ModifierGroup.toOutgoingModifier(): List<OutgoingModifier> {
    return items.map {
        OutgoingModifier(
            productId = it.id,
            amount = DEFAULT_AMOUNT,
            price = it.price.toDouble(),
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
    paymentType: String,
): String {
    val utensilsPart = when {
        utensils.noNeedUtensils -> OrderConstants.NO_UTENSILS_COMMENT
        utensils.chosenUtensils.isNotEmpty() -> UTENSILS_NEED_PREFIX +
                utensils.chosenUtensils.joinToString { it.stringName }

        else -> null
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

