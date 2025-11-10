package com.mandarinkafe.mandarin.features.cart.data.local

data class CartItemInsertParams(
    val id: String, // id CartItem, генерируется в приложении при добавлении блюда в корзину
    val mealId: String, // id базового блюда из iiko, для валидации из актуального меню
    val addsJson: String, // JSON-строка с List<String> id добавок
    val modifiersJson: String, // JSON-строка с List<ModifierGroup>
    val quantity: Long,
    val comment: String?,
    val createdAt: Long, // время создания позиции
    val updatedAt: Long, // время последнего изменения позиции
)