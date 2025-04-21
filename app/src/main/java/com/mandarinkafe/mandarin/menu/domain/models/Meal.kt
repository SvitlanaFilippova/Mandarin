package com.mandarinkafe.mandarin.menu.domain.models

data class Meal(
    val id: String,
    val name: String,
    val description: String,
    val weight: Int,
    val price: Int,
    val imageUrl: String,
    var isFavorite: Boolean,
    val tags: List<Tag>,
    val labels: List<Label>,
    val isHidden: Boolean,
    val isEditable: Boolean
)