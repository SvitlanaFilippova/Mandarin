package com.mandarinkafe.mandarin.menu.data.dto

data class RestrictionsDto(
    val byDefault: Int,
    val freeQuantity: Int,
    val hideIfDefaultQuantity: Boolean,
    val maxQuantity: Int,
    val minQuantity: Int
)