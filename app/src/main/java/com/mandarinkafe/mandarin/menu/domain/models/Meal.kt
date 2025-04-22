package com.mandarinkafe.mandarin.menu.domain.models

data class Meal(
    override val id: String,
    override val name: String,
    override val weight: Int,
    override val price: Int,
    override val isHidden: Boolean,
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


    /**
    Показывает, должен ли открываться дополнительный экран с кастомизацией блюда
     */
    val isEditable: Boolean,

    /**
    В списке будут хранится выбранные добавки для пиццы
     */
    val adds: List<MealAdditional> = emptyList<MealAdditional>()
) : BaseMeal()

