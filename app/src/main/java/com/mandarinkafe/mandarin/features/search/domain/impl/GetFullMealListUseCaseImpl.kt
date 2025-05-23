package com.mandarinkafe.mandarin.features.search.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.features.search.domain.usecase.GetFullMealListUseCase
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFullMealListUseCaseImpl(
    private val repository: MenuRepository
) : GetFullMealListUseCase {

    override fun invoke(): Flow<Resource<List<Meal>>> {
        repository.getMenu()
        return repository.menu.map { result ->
            when (result) {
                is Resource.Success -> {
                    val visibleMenu = result.data?.filterNot { it.isHidden }.orEmpty()
                    val allMeals = visibleMenu
                        .flatMap { collectMealsFromCategory(it) }
                        .distinctBy { it.id } // если нужно убрать дубликаты
                    Resource.Success(allMeals)
                }

                is Resource.Error -> Resource.Error(result.message.toString())
                is Resource.Loading -> Resource.Loading()
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