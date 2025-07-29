package com.mandarinkafe.mandarin.core.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.GetInitialDataUseCase
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetInitialDataUseCaseImpl(
    private val menuCache: MenuCache,
    private val bannersRepository: BannersRepository
) : GetInitialDataUseCase {
    override suspend operator fun invoke(): Flow<Resource<List<MealCategory>>> {
        menuCache.fetchMenuIfNeeded()
        bannersRepository.loadBannersCsv()

        return menuCache.menu.map { result ->
            when (result) {
                is Resource.Success -> {
                    val filtered = result.data?.filterNot { it.isHidden }
                    Resource.Success(filtered ?: emptyList())
                }

                else -> result
            }
        }
    }
}