package com.mandarinkafe.mandarin.features.order.presentation.viewmodel.state

import com.mandarinkafe.mandarin.features.order.domain.models.Utensil

data class Utensils(
    val noNeedUtensils: Boolean = false,
    val chosenUtensils: List<Utensil> = emptyList()
)