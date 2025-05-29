package com.mandarinkafe.mandarin.features.cart.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetCommonRecommendsUseCase
import com.mandarinkafe.mandarin.features.menu.domain.usecase.CategoryFilter
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorEmptyData
import com.mandarinkafe.mandarin.util.Resource.ErrorNoInternet
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import com.mandarinkafe.mandarin.util.di.Recommends
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class GetCommonRecommendsUseCaseImpl(
    private val cache: MenuCache,
    @Recommends private val recommendsFilter: CategoryFilter,
) : GetCommonRecommendsUseCase {

    override suspend operator fun invoke(): Resource<List<Meal>> {
        // Запрашиваем меню (если нужно)
        cache.fetchMenuIfNeeded()

        // Ждём первого «не-Loading/Idle» состояния
        val result = cache.menu
            .filter { it !is Loading && it !is Idle }
            .first()

        // Мапим на Resource<List<Meal>>
        return when (result) {
            is Success -> {
                // фильтруем категории, разворачиваем все meals
                val meals = result.data
                    .orEmpty()
                    .filter { recommendsFilter.isMatch(it) }
                    .flatMap { it.meals.orEmpty() }
                Success(meals)
            }

            is ErrorEmptyData -> ErrorEmptyData()
            is ErrorNoInternet -> ErrorNoInternet()
            is ErrorOther -> ErrorOther(result.message.orEmpty())
            else -> ErrorOther("Неизвестная ошибка")
        }
    }
}
