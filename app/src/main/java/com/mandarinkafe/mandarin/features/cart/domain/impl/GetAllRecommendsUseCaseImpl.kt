package com.mandarinkafe.mandarin.features.cart.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetAllRecommendsUseCase
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetCommonRecommendsUseCase
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetRecommendsUseCase
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetAllRecommendsUseCaseImpl(
    private val common: GetCommonRecommendsUseCase,
    private val cartBased: GetRecommendsUseCase
) : GetAllRecommendsUseCase {
    override suspend fun invoke(cartItems: Set<Meal>): Resource<List<Meal>> {
        // Запрашиваем оба набора параллельно
        val commonResDeferred = coroutineScope { async { common() } }
        val cartResDeferred = coroutineScope { async { cartBased(cartItems) } }
        val commonRes = commonResDeferred.await()
        val cartRes = cartResDeferred.await()

        // Если хотя бы один из них – ErrorNoInternet, возвращаем NoInternet
        if (commonRes is Resource.ErrorNoInternet || cartRes is Resource.ErrorNoInternet) {
            return Resource.ErrorNoInternet()
        }
        // Если оба набора пусты – можно вернуть EmptyData
        val commonList = (commonRes as? Resource.Success)?.data.orEmpty()
        val cartList = (cartRes as? Resource.Success)?.data.orEmpty()
        val merged = (commonList + cartList)
            .distinctBy { it.id }     // удаляем дубликаты
            .toList()
        return if (merged.isEmpty()) {
            Resource.ErrorEmptyData()
        } else {
            Resource.Success(merged)
        }
    }
}
