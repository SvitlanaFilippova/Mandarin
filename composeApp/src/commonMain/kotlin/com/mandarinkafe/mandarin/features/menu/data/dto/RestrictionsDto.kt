package com.mandarinkafe.mandarin.features.menu.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RestrictionsDto(
    val maxQuantity: Int,
    val minQuantity: Int
)





