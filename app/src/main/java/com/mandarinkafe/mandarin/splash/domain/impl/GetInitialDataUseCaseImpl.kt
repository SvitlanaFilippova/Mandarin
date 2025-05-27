package com.mandarinkafe.mandarin.splash.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.splash.domain.usecase.GetInitialDataUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.Error
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetInitialDataUseCaseImpl(private val menuCache: MenuCache) : GetInitialDataUseCase {
    override suspend operator fun invoke(): Flow<Resource<List<MealCategory>>> {
        menuCache.fetchMenuIfNeeded()

        return menuCache.menu.map { result ->
            when (result) {
                is Success -> {
                    val filtered = result.data?.filterNot { it.isHidden }
                    Success(filtered ?: emptyList())
                }

                is Error -> result
                is Loading -> result
                is Idle -> result
            }
        }
    }
}