package com.mandarinkafe.mandarin.features.favorites.data.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.FavoriteRecord
import com.mandarinkafe.mandarin.core.domain.models.id
import com.mandarinkafe.mandarin.features.cart.data.Mapper.validateBy
import com.mandarinkafe.mandarin.features.menu.domain.toMealAdditional
import com.mandarinkafe.mandarin.util.Constants.MENU_WAIT_TIMEOUT
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeoutOrNull

class FavoritesValidator(
    private val menuCache: MenuCache,
) {
    suspend operator fun invoke(raw: Set<FavoriteRecord>): Resource<List<CustomizedMeal>> {
        return try {
            // Ждём до MENU_WAIT_TIMEOUT пока меню станет не Loading/Idle
            // Используем allMenu для валидации, чтобы находить скрытые блюда
            val menuResource = withTimeoutOrNull(MENU_WAIT_TIMEOUT) {
                menuCache.allMenu
                    .firstOrNull { it !is Resource.Loading && it !is Resource.Idle }
            } ?: menuCache.allMenu.value // если таймаут, берём последнее известное состояние

            when (menuResource) {
                is Resource.Success -> {
                    val result = processRecords(raw)
                    Resource.Success(
                        result.validPairs
                            .sortedWith(
                                compareBy<Pair<FavoriteRecord, CustomizedMeal>> { it.second.meal.isHidden }
                                    .thenByDescending { it.first.updatedAt }
                            )
                            .map { it.second }
                            .distinctBy { it.id }
                    )
                }

                is Resource.ErrorEmptyData -> Resource.ErrorEmptyData()
                is Resource.ErrorNoInternet -> Resource.ErrorNoInternet()
                is Resource.ErrorOther -> Resource.ErrorOther(menuResource.message ?: "Ошибка меню")
                is Resource.Loading -> Resource.Loading()
                is Resource.Idle -> Resource.Idle()

            }
        } catch (e: Exception) {
            Resource.ErrorOther(e.message ?: "Favorites validation error")
        }
    }


    private fun processRecords(
        raw: Set<FavoriteRecord>,
    ): ValidationResult {
        val validPairs = mutableListOf<Pair<FavoriteRecord, CustomizedMeal>>()

        for (record in raw) {
            // Используем getMealByIdFromAllMenu для поиска в неотфильтрованном меню
            val fullMeal = menuCache.getMealByIdFromAllMenu(record.mealId)
            if (fullMeal == null) {
                continue
            }

            when (record) {
                is FavoriteRecord.Base -> {
                    val customized = CustomizedMeal(
                        meal = fullMeal,
                        adds = emptyList(),
                        modifiers = emptyList()
                    )
                    validPairs += record to customized
                }

                is FavoriteRecord.Custom -> {
                    val validAdds = record.addsIds.mapNotNull { id ->
                        menuCache.getMealByIdFromAllMenu(id)?.toMealAdditional()
                    }

                    val validMods = record.modifiers.validateBy(fullMeal.modifiers)

                    val cleaned = record.copy(
                        addsIds = validAdds.map { it.id },
                        modifiers = validMods
                    )
                    val customized = CustomizedMeal(
                        meal = fullMeal,
                        adds = validAdds,
                        modifiers = validMods
                    )
                    validPairs += cleaned to customized
                }
            }
        }

        return ValidationResult(
            validPairs = validPairs,
        )
    }

    private data class ValidationResult(
        val validPairs: List<Pair<FavoriteRecord, CustomizedMeal>>,
    )
}






