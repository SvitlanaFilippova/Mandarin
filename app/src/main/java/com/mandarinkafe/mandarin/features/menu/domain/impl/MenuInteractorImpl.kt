package com.mandarinkafe.mandarin.features.menu.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditionalCategory
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.features.menu.domain.usecase.CategoryFilter
import com.mandarinkafe.mandarin.features.menu.domain.usecase.MenuInteractor
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.di.Addons
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MenuInteractorImpl(
    private val repository: MenuRepository,
    @Addons private val addonsFilter: CategoryFilter
) : MenuInteractor {

    override fun getMenu(): Flow<Pair<List<MealCategory>?, String?>> = repository.getMenu()
        .map { result ->
            when (result) {
                is Resource.Success -> {
                    // Фильтруем все категории, которые не должны отображаться в общем меню (флаг isHidden)
                    val visibleMenu = result.data?.filterNot { it.isHidden }
                    Pair(visibleMenu, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }

                is Resource.Loading -> {
                    Pair(null, null)
                }
            }
        }

    override fun getAddons(): Flow<Pair<List<MealAdditionalCategory>?, String?>> =
        repository.getMenu()
            .map { result ->
                when (result) {
                    is Resource.Success -> {
                        val addonsParents = result.data?.filter { addonsFilter.isMatch(it) }
                        val addonsCategories: List<MealCategory> = addonsParents
                            ?.flatMap {
                                it.subCategories ?: emptyList()
                            } ?: emptyList()

                        Pair(addonsCategories.map { it.toMealAdditionalCategory() }, null)
                    }

                    is Resource.Error -> {
                        Pair(null, result.message)
                    }

                    is Resource.Loading -> {
                        Pair(null, null)
                    }
                }
            }

    // метод, чтобы принудительно перезагрузить меню
    override suspend fun forceRefresh() {
        repository.forceRefresh()
    }
}
