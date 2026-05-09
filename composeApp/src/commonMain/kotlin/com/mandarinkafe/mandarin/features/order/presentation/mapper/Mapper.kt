package com.mandarinkafe.mandarin.features.order.presentation.mapper

import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_TECH_PART
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.DIVIDER_FOR_USER_COMMENT
import com.mandarinkafe.mandarin.features.order.data.mapper.OrderConstants.UTENSILS_NEED_PREFIX
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrder
import com.mandarinkafe.mandarin.features.order.domain.models.PaymentType
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderState
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state.Utensils
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_BANK_CODE
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_CASH_CODE
import com.mandarinkafe.mandarin.util.Constants.PAYMENT_ONLINE_CODE


fun OrderState.toDomain(paymentType: PaymentType): OutgoingOrder {
    val cash = paymentType.code == PAYMENT_CASH_CODE
    val cashNoChange = if (cash) paymentInfo.noChange else null
    val cashChangeFrom = if (cash) paymentInfo.changeFrom else ""
    val paymentLine = buildPaymentAndChangeTechLine(
        paymentTypeCode = paymentType.code,
        noChange = cashNoChange,
        changeFrom = cashChangeFrom,
    )
    val fullComment = buildFullComment(
        userComment = comment.trim(),
        utensils = utensils,
        paymentLine = paymentLine,
    )
    val address = if (deliveryInfo.isPickup) null else deliveryInfo.chosenAddress
    // Исключаем скрытые блюда из передачи в заказ
    val visibleItems = cartSummary.items.filter { !it.customizedMeal.meal.isHidden }
    return OutgoingOrder(
        name = userInfo.name.trim(),
        phone = userInfo.phone,
        deliveryType = deliveryInfo.deliveryType
            ?: error("deliveryType is null"),
        chosenAddress = address,
        paymentType = paymentType,
        deliveryPointPaymentSuffix = buildDeliveryPointPaymentSuffix(
            paymentTypeCode = paymentType.code,
            cashNoChange = cashNoChange,
            cashChangeFrom = cashChangeFrom,
        ),
        comment = fullComment,
        items = visibleItems,
        deliveryRealCost = deliveryCost,
        totalOrderSum = totalOrderSum,
        discountPercent = cartSummary.discountPercent,
        deliveryZoneID = deliveryInfo.deliveryZone?.id,
    )
}

/**
 * Суффикс комментария к адресу для курьера: при наличных — `[оплата: наличные, …сдача]` в одних скобках;
 * при прочих способах — строка как в [buildPaymentAndChangeTechLine].
 */
private fun buildDeliveryPointPaymentSuffix(
    paymentTypeCode: String,
    cashNoChange: Boolean?,
    cashChangeFrom: String,
): String {
    val label = paymentLabelForComment(paymentTypeCode)
    if (paymentTypeCode.equals(PAYMENT_CASH_CODE, ignoreCase = true)) {
        val changeSegment = when {
            cashNoChange == null -> null
            cashNoChange -> OrderConstants.NO_CHANGE_COMMENT
            cashChangeFrom.isNotEmpty() ->
                OrderConstants.CHANGE_FROM_COMMENT_PREFIX + cashChangeFrom

            else -> null
        }
        return buildString {
            append("[оплата: ")
            append(label)
            if (changeSegment != null) {
                append(", ")
                append(changeSegment)
            }
            append(']')
        }
    }
    return OrderConstants.PAYMENT_TYPE_TECH_FORMAT.replace("%s", label)
}

private fun paymentLabelForComment(paymentTypeCode: String): String =
    when (paymentTypeCode.uppercase()) {
        PAYMENT_CASH_CODE -> "наличные"
        PAYMENT_BANK_CODE -> "картой при получении"
        PAYMENT_ONLINE_CODE -> "онлайн-оплата"
        else -> paymentTypeCode.lowercase()
    }

/**
 * Техническая часть комментария к заказу: оплата и при наличных — сдача (через `. `).
 */
private fun buildPaymentAndChangeTechLine(
    paymentTypeCode: String,
    noChange: Boolean?,
    changeFrom: String,
): String {
    val paymentLabel = paymentLabelForComment(paymentTypeCode)
    val paymentTypePart = OrderConstants.PAYMENT_TYPE_TECH_FORMAT.replace("%s", paymentLabel)
    val changePart = when {
        noChange == null -> null
        noChange -> OrderConstants.NO_CHANGE_COMMENT
        changeFrom.isNotEmpty() -> OrderConstants.CHANGE_FROM_COMMENT_PREFIX + changeFrom
        else -> null
    }
    return listOfNotNull(paymentTypePart, changePart).joinToString(DIVIDER_FOR_TECH_PART)
}

private fun buildFullComment(
    userComment: String,
    utensils: Utensils,
    paymentLine: String,
): String {
    val utensilsPart = when {
        utensils.noNeedUtensils -> OrderConstants.NO_UTENSILS_COMMENT
        utensils.chosenUtensils.isNotEmpty() -> UTENSILS_NEED_PREFIX +
                utensils.chosenUtensils.joinToString { it.stringName }

        else -> null
    }

    val techParts = listOfNotNull(paymentLine, utensilsPart)
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

