package com.mandarinkafe.mandarin.core.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.GetInitialDataUseCase
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.address.domain.api.DeliveryAreaRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.auth.domain.api.SyncUserDataUseCase
import com.mandarinkafe.mandarin.features.menu.domain.api.AnnouncementsRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GetInitialDataUseCaseImpl(
    private val authRepository: AuthRepository,
    private val menuCache: MenuCache,
    private val bannersRepository: BannersRepository,
    private val announcementsRepository: AnnouncementsRepository,
    private val deliveryAreaRepository: DeliveryAreaRepository,
    private val syncUserDataUseCase: SyncUserDataUseCase,
) : GetInitialDataUseCase {
    override suspend operator fun invoke(): Flow<Resource<List<MealCategory>>> = flow {
        // 1. Сначала проверяем и валидируем токены
        val isAuthorized = authRepository.initializeAuth()

        // 2. Загружаем меню
        menuCache.fetchMenuIfNeeded()

        // 3. Если пользователь авторизован, синхронизируем данные (корзина, избранное)
        if (isAuthorized) {
            syncUserDataUseCase()
        }

        // 3. Параллельно грузим всё остальное
        coroutineScope {
            launch { bannersRepository.loadBanners() }
            launch { announcementsRepository.loadAnnouncements() }
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