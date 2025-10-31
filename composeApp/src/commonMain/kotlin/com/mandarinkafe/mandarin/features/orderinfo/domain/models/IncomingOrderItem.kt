package com.mandarinkafe.mandarin.features.orderinfo.domain.models

import kotlin.math.abs

data class IncomingOrderItem(
    val id: String,
    val name: String,
    val amount: Double,
    val chosenModifiers: List<IncomingModifier> = emptyList(),
    val chosenAdds: List<IncomingMealAdditional> = emptyList(),
    val price: Double,
    val discountedPrice: Double?,
    val positionId: String?,
    val isDeleted: Boolean = false,
    val comment: String,
    val isValidated: Boolean = false,
) {
    // Цена за одну единицу без скидки (включая модификаторы и добавки)
    val unitPrice: Double
        get() = price + chosenModifiers.sumOf { it.price } + chosenAdds.sumOf { it.price }

    // Общая цена без скидки
    val totalPrice: Double
        get() = unitPrice * amount

    // Цена за одну единицу со скидкой
    val unitDiscountedPrice: Double
        get() = (discountedPrice ?: price) +
                chosenModifiers.sumOf { it.discountedPrice ?: it.price } +
                chosenAdds.sumOf { it.discountedPrice ?: it.price }

    // Общая цена со скидкой
    val totalDiscountedPrice: Double
        get() = unitDiscountedPrice * amount

    val isDiscounted: Boolean
        get() {
            val epsilon = EPSILON
            return abs(unitPrice - unitDiscountedPrice) > epsilon
        }

    private companion object {
        const val EPSILON = 0.01
    }
}