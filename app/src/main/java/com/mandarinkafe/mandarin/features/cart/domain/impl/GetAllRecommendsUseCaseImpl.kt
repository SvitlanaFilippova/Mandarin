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

    override suspend fun invoke(cartItems: Set<Meal>): Resource<List<Meal>> = coroutineScope {
        // Запрашиваем оба набора параллельно
        val commonResDeferred = async { common() }
        val cartResDeferred = async { cartBased(cartItems) }

        val commonRes = commonResDeferred.await()
        val cartRes = cartResDeferred.await()

        // Если хоть один – NoInternet → возвращаем NoInternet
        if (commonRes is Resource.ErrorNoInternet || cartRes is Resource.ErrorNoInternet) {
            return@coroutineScope Resource.ErrorNoInternet()
        }

        // Получаем списки (успешные или пустые)
        val commonList = (commonRes as? Resource.Success)?.data.orEmpty()
        val cartList = (cartRes as? Resource.Success)?.data.orEmpty()

        // ids элементов в корзине
        val inCartIds = cartItems.map { it.id }.toSet()

        // Объединяем, удаляем дубликаты, фильтруем уже в корзине
        val merged = (cartList + commonList)
            .distinctBy { it.id }
            .filterNot { it.id in inCartIds }

        return@coroutineScope if (merged.isEmpty()) {
            Resource.ErrorEmptyData()
        } else {
            Resource.Success(merged)
        }
    }
}
