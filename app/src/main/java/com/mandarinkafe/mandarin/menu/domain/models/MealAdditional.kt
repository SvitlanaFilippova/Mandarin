package com.mandarinkafe.mandarin.menu.domain.models

data class MealAdditional(
    override val id: String,
    override val name: String,
    override val weight: Int,
    override val price: Int,
    override val isHidden: Boolean
) : BaseMeal()