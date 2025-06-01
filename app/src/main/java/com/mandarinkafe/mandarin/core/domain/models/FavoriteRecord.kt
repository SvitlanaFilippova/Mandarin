package com.mandarinkafe.mandarin.core.domain.models

sealed class FavoriteRecord {
    abstract val mealId: String
    abstract val timestamp: Long

    /** Простая запись — без кастомизации */
    data class Base(
        override val mealId: String,
        override val timestamp: Long
    ) : FavoriteRecord() {

        override fun equals(other: Any?): Boolean {
            return other is Base && mealId == other.mealId
        }

        override fun hashCode(): Int {
            return mealId.hashCode()
        }
    }

    /** Кастомизированная запись */
    data class Custom(
        override val mealId: String,
        override val timestamp: Long,
        val addsIds: List<String>,
        val modifiers: List<ModifierGroup>
    ) : FavoriteRecord() {

        override fun equals(other: Any?): Boolean {
            return other is Custom &&
                    mealId == other.mealId &&
                    addsIds.toSet() == other.addsIds.toSet() &&
                    modifiers.toSet() == other.modifiers.toSet()
        }

        override fun hashCode(): Int {
            var result = mealId.hashCode()
            result = 31 * result + addsIds.toSet().hashCode()
            result = 31 * result + modifiers.toSet().hashCode()
            return result
        }
    }
}