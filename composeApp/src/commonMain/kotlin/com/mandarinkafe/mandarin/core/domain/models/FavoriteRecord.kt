package com.mandarinkafe.mandarin.core.domain.models

sealed class FavoriteRecord {
    abstract val mealId: String
    abstract val createdAt: Long
    abstract val updatedAt: Long

    /** Простая запись — без кастомизации */
    data class Base(
        override val mealId: String,
        override val createdAt: Long,
        override val updatedAt: Long,
    ) : FavoriteRecord() {

        override fun equals(other: Any?): Boolean {
            return other is Base && mealId == other.mealId
            // не сравниваем timestamp
        }

        override fun hashCode(): Int {
            return mealId.hashCode()
        }
    }

    /** Кастомизированная запись */
    data class Custom(
        override val mealId: String,
        override val createdAt: Long,
        override val updatedAt: Long,
        val addsIds: List<String>,
        val modifiers: List<ModifierGroup>,
    ) : FavoriteRecord() {

        override fun equals(other: Any?): Boolean {
            return other is Custom &&
                    mealId == other.mealId &&
                    addsIds.toSet() == other.addsIds.toSet() &&
                    modifiers.hasSameContentAs(other.modifiers)
            // не сравниваем timestamp
        }

        override fun hashCode(): Int {
            var result = mealId.hashCode()
            result = 31 * result + addsIds.toSet().hashCode()
            // Используем только ID групп и элементов для hashCode
            result = 31 * result + modifiers.map { it.id to it.items.map { item -> item.id }.toSet() }.hashCode()
            return result
        }
    }
}