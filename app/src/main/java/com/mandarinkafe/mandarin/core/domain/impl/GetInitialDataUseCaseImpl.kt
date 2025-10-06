package com.mandarinkafe.mandarin.core.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.GetInitialDataUseCase
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.address.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.CategoryDiscountRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GetInitialDataUseCaseImpl(
    private val menuCache: MenuCache,
    private val bannersRepository: BannersRepository,
    private val categoryDiscountRepository: CategoryDiscountRepository,
    private val deliveryAreaRepository: DeliveryAreaRepository
) : GetInitialDataUseCase {
    override suspend operator fun invoke(): Flow<Resource<List<MealCategory>>> = flow {
        // сначала меню
        menuCache.fetchMenuIfNeeded()

        // параллельно грузим всё остальное
        coroutineScope {
            launch { bannersRepository.loadBanners() }
            launch { categoryDiscountRepository.refreshFromApi() }
            launch { deliveryAreaRepository.getAllAreas() }
        }

        emitAll(
            menuCache.mainMenu.map { result ->
                when (result) {
                    is Resource.Success -> {
                        val filtered = result.data?.filterNot { it.isHidden }
                        Resource.Success(filtered ?: emptyList())
                    }

                    else -> result
                }
            }
        )
    }
}