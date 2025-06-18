package com.mandarinkafe.mandarin.core.domain.models.extensions

import com.mandarinkafe.mandarin.core.domain.models.Meal

/**
 * Проверяет, можно ли как-то кастомизировать блюдо — добавить необязательные модификаторы или добавки.
 */
val Meal.isCustomizable: Boolean
    get() = (this.isAddable || this.isModifiable)

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

fun Meal.isFavorite(favoriteIds: Set<String>): Boolean {
    return favoriteIds.contains(id)
}
