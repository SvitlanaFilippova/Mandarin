package com.mandarinkafe.mandarin.features.favorites.domain.models

import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup

sealed class FavoriteRecord {
    abstract val mealId: String
    abstract val timestamp: Long

    /** Простая запись — без кастомизации */
    data class Base(
        override val mealId: String,
        override val timestamp: Long
    ) : FavoriteRecord()

    /** Кастомизированная запись */
    data class Custom(
        override val mealId: String,
        override val timestamp: Long,
        val addsIds: List<String>,
        val modifiers: List<ModifierGroup>
    ) : FavoriteRecord()
}