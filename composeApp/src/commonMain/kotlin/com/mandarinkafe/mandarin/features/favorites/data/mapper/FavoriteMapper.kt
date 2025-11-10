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
            is FavoriteRecord.Base -> StoredFavoriteMeal(
                mealId = mealId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
            is FavoriteRecord.Custom -> StoredFavoriteMeal(
                mealId = mealId,
                createdAt = createdAt,
                updatedAt = updatedAt,
                addsIds = addsIds,
                modifiers = modifiers
            )
        }
    }

    fun Set<StoredFavoriteMeal>.toFavoriteRecords(): MutableSet<FavoriteRecord> =
        this.map {
            if (it.addsIds.isEmpty() && it.modifiers.isEmpty()) {
                FavoriteRecord.Base(
                    mealId = it.mealId,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            } else {
                FavoriteRecord.Custom(
                    mealId = it.mealId,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    addsIds = it.addsIds,
                    modifiers = it.modifiers
                )
            }
        }.toMutableSet()

    fun CustomizedMeal.toFavoriteRecord(createdAt: Long, updatedAt: Long = 0L): FavoriteRecord {
        return if (this.isCustomized) {
            FavoriteRecord.Custom(
                mealId = meal.id,
                createdAt = createdAt,
                updatedAt = updatedAt,
                addsIds = adds.map { it.id },
                modifiers = modifiers
            )
        } else {
            FavoriteRecord.Base(
                mealId = meal.id,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun Meal.toFavoriteRecord(createdAt: Long, updatedAt: Long = 0L): FavoriteRecord {
        return FavoriteRecord.Base(
            mealId = id,
            createdAt = createdAt,
            updatedAt = updatedAt
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
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun FavoriteDto.toStored(menuCache: MenuCache): StoredFavoriteMeal {
        val resolvedModifiers = modifierIds.mapNotNull { (groupId, itemIds) ->
            menuCache.findModifierGroup(groupId, itemIds)
        }
        return StoredFavoriteMeal(
            mealId = mealId,
            createdAt = createdAt,
            updatedAt = updatedAt,
            addsIds = addsIds,
            modifiers = resolvedModifiers
        )
    }
}
