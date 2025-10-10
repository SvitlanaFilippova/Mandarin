package com.mandarinkafe.mandarin.features.cart.presentation.viewmodel

import com.mandarinkafe.mandarin.core.domain.models.Meal

data class Recommends(
    val mainRecommends: List<Meal> = emptyList(),
    val separateRecommends: List<Meal> = emptyList()
)
