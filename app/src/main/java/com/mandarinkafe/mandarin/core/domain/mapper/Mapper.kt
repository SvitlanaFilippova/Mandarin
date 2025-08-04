package com.mandarinkafe.mandarin.core.domain.mapper

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.order.domain.models.OutgoingOrderItem

object Mapper {

    fun Meal.toCustomizedMeal() = CustomizedMeal(
        meal = this
    )

    fun OutgoingOrderItem.toDomain(): OutgoingOrderItem {
        return OutgoingOrderItem(
            productId = productId,
            modifiers = modifiers,
            price = price,
            amount = amount,
            type = type,
            comment = comment
        )
    }
}