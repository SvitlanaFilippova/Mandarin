package com.mandarinkafe.mandarin.core.domain.models.extensions

import com.mandarinkafe.mandarin.core.domain.models.Meal

fun Meal.isCustomizable() = (isAddable || isModifiable)