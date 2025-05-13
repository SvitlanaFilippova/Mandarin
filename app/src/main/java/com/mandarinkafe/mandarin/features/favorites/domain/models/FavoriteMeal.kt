package com.mandarinkafe.mandarin.features.favorites.domain.models

import com.mandarinkafe.mandarin.core.domain.models.EditableType
import com.mandarinkafe.mandarin.core.domain.models.Label
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.Tag

data class FavoriteMeal(
    val id: String,
    val name: String,
    val description: String,
    val weight: Int,
    val price: Int,
    val imageUrl: String,
    var isFavorite: Boolean,
    val tags: List<Tag>, // Используем классы из другой фичи, т.к. эта фича зависит от меню
    val labels: List<Label>,
    val isHidden: Boolean,
    val editableType: EditableType?,
    val modifiers: List<ModifierGroup>,
)
