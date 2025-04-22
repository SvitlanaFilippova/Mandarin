package com.mandarinkafe.mandarin.menu.domain.models

data class Meal(
    val id: String,
    val name: String,
    val description: String,
    val weight: Int,
    val price: Int,
    val imageUrl: String,
    var isFavorite: Boolean,

    /**
    Внутренние теги для особой обработки блюда в меню
     */
    val tags: List<Tag>,

    /**
    Ярлыки для от ображения в меню и для фильтрации
     */
    val labels: List<Label>,

    /**
    Показывает, должно ли блюдо отображаться в общем меню
     */
    val isHidden: Boolean,

    /**
    Показывает, должен ли открываться дополнительный экран с кастомизацией блюда
     */
    val isEditable: Boolean
)