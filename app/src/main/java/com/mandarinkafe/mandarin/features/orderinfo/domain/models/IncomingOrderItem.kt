package com.mandarinkafe.mandarin.features.orderinfo.domain.models

data class IncomingOrderItem(
    val id: String,
    val name: String,
    val amount: Double,
    val chosenModifiers: List<IncomingModifier> = emptyList(),
    val chosenAdds: List<IncomingMealAdditional> = emptyList(),
    val price: Double,
    val discountedPrice: Double?,
    val positionId: String?,
    val deleted: DeletionInfo = DeletionInfo(),
    val comment: String
) {
    val totalPrice: Double
        get() = price + chosenModifiers.sumOf { it.price } + chosenAdds.sumOf { it.price }
    val totalDiscountedPrice: Double
        get() = (discountedPrice ?: price) +
                chosenModifiers.sumOf { it.discountedPrice ?: it.price } +
                chosenAdds.sumOf { it.discountedPrice ?: it.price }

    val isDiscounted = totalPrice != totalDiscountedPrice
}

