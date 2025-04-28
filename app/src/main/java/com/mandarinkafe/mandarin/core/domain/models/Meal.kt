package com.mandarinkafe.mandarin.core.domain.models

data class Meal(
    val id: String,
    val name: String,
    val weight: Int,
    val price: Int,
    val imageUrl: String,
    var isFavorite: Boolean,
    val description: String,

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
    Тип дополнительного экрана, который должен открываться при клике на блюдо
     */
    val editableType: EditableType?,

    /**
    Группы модификаторов, доступных для блюда
     */
    val modifiers: List<ModifierGroup>,
)