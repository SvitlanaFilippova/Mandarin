package com.mandarinkafe.mandarin.features.orderconfirmation.domain.models

data class IncomingOrderItem(
    val id: String,
    val name: String,
    val amount: Double,
    val chosenModifiers: List<IncomingModifier> = emptyList(),
    val chosenAdds: List<IncomingMealAdditional> = emptyList(),
    val price: Double,
    val positionId: String?,
    val deleted: DeletionInfo = DeletionInfo(),
    val comment: String
) {
    val totalPrice: Double
        get() = price + chosenModifiers.sumOf { it.price } + chosenAdds.sumOf { it.price }
}


