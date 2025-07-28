package com.mandarinkafe.mandarin.features.favorites.data.mapper

import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal

object FavoriteMapper {

    fun FavoriteRecord.toStored(): StoredFavoriteMeal {
        return when (this) {
            is FavoriteRecord.Base -> StoredFavoriteMeal(mealId = mealId, timestamp = timestamp)
            is FavoriteRecord.Custom -> StoredFavoriteMeal(
                mealId = mealId,
                addsIds = addsIds,
                modifiers = modifiers,
                timestamp = timestamp
            )
        }
    }

    fun Set<StoredFavoriteMeal>.toFavoriteRecords(): Set<FavoriteRecord> =
        this.map {
            if (it.addsIds.isEmpty() && it.modifiers.isEmpty()) {
                FavoriteRecord.Base(
                    mealId = it.mealId,
                    timestamp = it.timestamp
                )
            } else {
                FavoriteRecord.Custom(
                    mealId = it.mealId,
                    addsIds = it.addsIds,
                    modifiers = it.modifiers,
                    timestamp = it.timestamp
                )
            }
        }.toSet()

    fun CustomizedMeal.toFavoriteRecord(timestamp: Long): FavoriteRecord {
        return if (this.isCustomized) {
            FavoriteRecord.Custom(
                mealId = meal.id,
                addsIds = adds.map { it.id },
                modifiers = modifiers,
                timestamp = timestamp
            )
        } else {
            FavoriteRecord.Base(
                mealId = meal.id,
                timestamp = timestamp
            )
        }
    }

    fun Meal.toFavoriteRecord(timestamp: Long): FavoriteRecord {
        return FavoriteRecord.Base(
            mealId = id,
            timestamp = timestamp
        )
    }
}

