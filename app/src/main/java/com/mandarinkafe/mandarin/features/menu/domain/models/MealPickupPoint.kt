package com.mandarinkafe.mandarin.features.menu.domain.models

import kotlinx.serialization.Serializable

@Serializable
enum class MealPickupPoint {
    PIZZERIA,
    CAFE,
    ANY
}