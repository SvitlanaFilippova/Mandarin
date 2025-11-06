package com.mandarinkafe.mandarin.features.favorites.data.mapper

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.isCustomized
import com.mandarinkafe.mandarin.features.favorites.data.models.StoredFavoriteMeal
import com.mandarinkafe.mandarin.features.favorites.data.network.dto.FavoriteDto

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

    fun Set<StoredFavoriteMeal>.toFavoriteRecords(): MutableSet<FavoriteRecord> =
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
        }.toMutableSet()

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

    fun StoredFavoriteMeal.toDto(): FavoriteDto {
        val modifierIds = modifiers.associate { group ->
            group.id to group.items.map { it.id }
        }
        return FavoriteDto(
            mealId = mealId,
            addsIds = addsIds,
            modifierIds = modifierIds,
            timestamp = timestamp
        )
    }

    fun FavoriteDto.toStored(menuCache: MenuCache): StoredFavoriteMeal {
        val resolvedModifiers = modifierIds.mapNotNull { (groupId, itemIds) ->
            menuCache.findModifierGroup(groupId, itemIds)
        }
        return StoredFavoriteMeal(
            mealId = mealId,
            addsIds = addsIds,
            modifiers = resolvedModifiers,
            timestamp = timestamp
        )
    }
}
