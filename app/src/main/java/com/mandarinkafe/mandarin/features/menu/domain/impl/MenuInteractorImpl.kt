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

    override fun getMenu(): Flow<Resource<List<MealCategory>>> {
        repository.getMenu()
        return repository.menu.map { result ->
            when (result) {
                is Resource.Success -> {
                    val filtered = result.data?.filterNot { it.isHidden }
                    Resource.Success(filtered ?: emptyList())
                }

                is Resource.Error -> result
                is Resource.Loading -> result
            }
        }
    }

    override fun getAddons(): Flow<Resource<List<MealAdditionalCategory>>> {
        return repository.menu.map { result ->
            when (result) {
                is Resource.Success -> {
                    val addonsParents = result.data?.filter { addonsFilter.isMatch(it) }
                    val addonsCategories: List<MealCategory> = addonsParents
                        ?.flatMap {
                            it.subCategories ?: emptyList()
                        } ?: emptyList()
                    Resource.Success(addonsCategories.map { it.toMealAdditionalCategory() })
                }

                is Resource.Error -> {
                    Resource.Error(result.message.toString())
                }

                is Resource.Loading -> {
                    Resource.Loading()
                }
            }
        }
    }

    // метод, чтобы принудительно перезагрузить меню
    override suspend fun forceRefresh() {
        repository.forceRefresh()
    }
}
