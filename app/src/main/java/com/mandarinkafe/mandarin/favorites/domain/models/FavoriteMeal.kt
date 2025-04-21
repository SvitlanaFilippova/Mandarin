package com.mandarinkafe.mandarin.favorites.domain.models

import com.mandarinkafe.mandarin.menu.domain.models.Label
import com.mandarinkafe.mandarin.menu.domain.models.Tag


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
    val isEditable: Boolean
)
