package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Stable

@Stable
data class Meal(
    val id: String,
    val name: String,
    val weight: Int,
    val price: Int,
    val imageUrl: String,
    val description: String,
    val sku: String,

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
    Группы модификаторов, доступных для блюда
     */
    val modifiers: List<ModifierGroup>,

    /**
    Применимы ли добавки
     */
    val isAddable: Boolean,

    /**
    Выбор опции обязателен
     */
    val requireSelection: Boolean,

    /**
    Модификаторы опциональны
     */
    val isModifiable: Boolean,

    /**
    Применимы ли скидки
     */
    val discountable: Boolean,

    /**
    Название родительской категории
     */
    val parentCategoryName: String,

    /**
    Название прародительской категории, если есть
     */
    val grandParentCategoryName: String?
)