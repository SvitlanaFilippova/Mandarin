package com.mandarinkafe.mandarin.features.ordershistory.presentation.models

enum class UiDateFilterType(
    val nameRes: String
) {
    TODAY(nameRes = "Сегодня"),
    YESTERDAY(nameRes = "Вчера"),
    WEEK(nameRes = "Неделя"),
    MONTH(nameRes = "Месяц"),
    ALL(nameRes = "Все")
}