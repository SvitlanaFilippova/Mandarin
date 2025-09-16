package com.mandarinkafe.mandarin.features.search.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.search.domain.usecase.GetFullMealListUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorEmptyData
import com.mandarinkafe.mandarin.util.Resource.ErrorNoInternet
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFullMealListUseCaseImpl(
    private val menuCache: MenuCache
) : GetFullMealListUseCase {

    override fun invoke(): Flow<Resource<List<Meal>>> {
        return menuCache.mainMenu.map { result ->
            when (result) {
                is Success -> {
                    val visibleMenu = result.data?.filterNot { it.isHidden }.orEmpty()
                    val allMeals = visibleMenu
                        .flatMap { collectMealsFromCategory(it) }
                        .distinctBy { it.id } // если нужно убрать дубликаты
                    Success(allMeals)
                }
                is Loading -> Loading()
                is Idle -> Loading()
                is ErrorOther -> ErrorOther(result.message.toString())
                is ErrorEmptyData -> ErrorEmptyData()
                is ErrorNoInternet -> ErrorNoInternet()
            }
        }
    }

    // Рекурсивная функция для сбора всех блюд из категории и подкатегорий
    private fun collectMealsFromCategory(category: MealCategory): List<Meal> {
        val directMeals = category.meals.orEmpty()
        val subMeals = category.subCategories
            ?.filterNot { it.isHidden }
            ?.flatMap { collectMealsFromCategory(it) }
            .orEmpty()
        return directMeals + subMeals
    }

}