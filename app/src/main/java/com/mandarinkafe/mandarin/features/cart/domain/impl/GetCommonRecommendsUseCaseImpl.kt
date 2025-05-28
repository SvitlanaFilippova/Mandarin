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
import kotlinx.coroutines.flow.map

class GetCommonRecommendsUseCaseImpl(
    private val cache: MenuCache,
    @Recommends private val recommendsFilter: CategoryFilter,
) : GetCommonRecommendsUseCase {

    override suspend operator fun invoke(): Resource<List<Meal>> {
        return cache.menu.map { result ->
            when (result) {
                is Success -> {
                    val filtered = result.data?.filter { recommendsFilter.isMatch(it) }.orEmpty()
                    Success(filtered.flatMap {
                        it.meals.orEmpty()
                    })
                }

                is ErrorEmptyData -> ErrorEmptyData()
                is ErrorNoInternet -> ErrorNoInternet()
                is ErrorOther -> ErrorOther(
                    message = result.message!!
                )

                is Idle -> Idle()
                is Loading -> Loading()
            }
        }
    }
}