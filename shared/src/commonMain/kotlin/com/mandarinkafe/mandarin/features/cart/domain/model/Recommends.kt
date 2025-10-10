package com.mandarinkafe.mandarin.features.cart.domain.model

import com.mandarinkafe.mandarin.core.domain.models.Meal
import kotlinx.serialization.Serializable

@Serializable
data class Recommends(
    val mainRecommends: List<Meal> = emptyList(),
    val separateRecommends: List<Meal> = emptyList()
)


