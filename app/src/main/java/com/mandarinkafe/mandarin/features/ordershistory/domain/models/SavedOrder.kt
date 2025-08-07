package com.mandarinkafe.mandarin.features.ordershistory.domain.models

data class SavedOrder(
    val id: String, // внутренний ID заказа
    val whenCreated: String = "", // когда заказ создан в формате HH:mm, dd.MM.yyyy
    val orderType: String = "", // доставка или самовывоз
    val address: String = "", // адрес доставки, если НЕ самовывоз
)