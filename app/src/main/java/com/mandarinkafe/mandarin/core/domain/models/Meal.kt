package com.mandarinkafe.mandarin.core.domain.models

import androidx.compose.runtime.Stable
import com.mandarinkafe.mandarin.features.menu.domain.models.MealPickupPoint

@Stable
data class Meal(
    val id: String,
    val name: String,
    val weight: Int,
    val measureUnitType: MeasureUnitType,
    val price: Int,
    val imageUrl: String,
    val imagePreviewUrl: String,
    val placeholderUrl: String,
    val description: String,
    val sku: String,
    val orderItemType: String,

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
    Заказ возможен только на самовывоз
     */
    val isPickupOnly: Boolean,

    /**
    Название родительской категории и всех прародительских, если есть
     */
    val categoryPath: List<String>,

    /**
    Точка самовывоза блюда
     */
    val pickupPoint: MealPickupPoint,

    /**
    Является ли позиция доставкой
     */
    val isDelivery: Boolean
)

/**
 * Проверяет, можно ли как-то кастомизировать блюдо — добавить необязательные модификаторы или добавки.
 */
val Meal.isCustomizable: Boolean
    get() = this.isAddable || this.isModifiable

/**
 * Проверяет, что у блюда:
 * - есть ровно один обязательный модификатор,
 * - этот модификатор поддерживает только один выбор (SingleChoice),
 * - нет других необязательных модификаторов,
 * - нет доступных добавок.
 *
 * Подходит для блюд, где пользователь должен просто выбрать один вариант из одного списка (например, выбрать тип сосиски у хотдога).
 */
fun Meal.isOnlySingleRequiredChoice(): Boolean {
    val requiredModifiers = modifiers.filter { it.isRequired }

    // Один обязательный модификатор с одиночным выбором
    val hasSingleRequiredGroup = requiredModifiers.size == 1 && requiredModifiers[0].isSingleChoice

    // Все модификаторы обязательные (нет необязательных)
    val noOptionalModifiers = modifiers.all { it.isRequired }

    // Нет доступных добавок
    val noAdds = !isAddable

    return hasSingleRequiredGroup && noOptionalModifiers && noAdds
}

fun Meal.isFavorite(favoriteIds: Set<String>) = favoriteIds.contains(id)
