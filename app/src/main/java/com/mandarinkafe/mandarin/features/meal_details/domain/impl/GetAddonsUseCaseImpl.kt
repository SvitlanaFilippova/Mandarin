package com.mandarinkafe.mandarin.features.meal_details.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.meal_details.domain.usecase.GetAddonsUseCase
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditionalCategory
import com.mandarinkafe.mandarin.features.menu.domain.models.MealAdditionalCategory
import com.mandarinkafe.mandarin.features.menu.domain.usecase.CategoryFilter
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorEmptyData
import com.mandarinkafe.mandarin.util.Resource.ErrorNoInternet
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import com.mandarinkafe.mandarin.util.di.Addons
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAddonsUseCaseImpl(
    private val cache: MenuCache,
    @Addons private val addonsFilter: CategoryFilter,
) : GetAddonsUseCase {

    override operator fun invoke(): Flow<Resource<List<MealAdditionalCategory>>> {
        return cache.menu.map { result ->
            when (result) {
                is Success -> {
                    val addonsParents = result.data?.filter { addonsFilter.isMatch(it) }
                    val addonsCategories: List<MealCategory> = addonsParents
                        ?.flatMap {
                            it.subCategories ?: emptyList()
                        } ?: emptyList()
                    Success(addonsCategories.map { it.toMealAdditionalCategory() })
                }

                is ErrorOther -> {
                    ErrorOther(result.message.toString())
                }

                is Loading -> {
                    Loading()
                }

                is Idle -> {
                    Loading()
                }

                is ErrorEmptyData -> ErrorEmptyData()
                is ErrorNoInternet -> ErrorNoInternet()
            }
        }
    }
}