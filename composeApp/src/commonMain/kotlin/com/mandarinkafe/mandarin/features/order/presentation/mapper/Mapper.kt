package com.mandarinkafe.mandarin.features.order.presentation.mapper

import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_TECH_PART
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_USER_COMMENT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.UTENSILS_NEED_PREFIX
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.Utensils


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
