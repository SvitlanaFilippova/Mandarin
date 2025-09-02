package com.mandarinkafe.mandarin.features.cart.domain.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetAllRecommendsUseCase
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetCartRecommendsUseCase
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetCommonRecommendsUseCase
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetAllRecommendsUseCaseImpl(
    private val common: GetCommonRecommendsUseCase,
    private val cartBased: GetCartRecommendsUseCase
) : GetAllRecommendsUseCase {

    companion object {
        private const val TAG = "GetAllRecommendsUC"
    }

    override suspend fun invoke(cartItems: Set<Meal>): Resource<List<Meal>> = coroutineScope {
        Log.d(TAG, "invoke() started, cartItems size: ${cartItems.size}")

        // Запрашиваем оба набора параллельно
        val commonResDeferred = async {
            Log.d(TAG, "Requesting common recommendations...")
            common()
        }
        val cartResDeferred = async {
            Log.d(TAG, "Requesting cart-based recommendations...")
            cartBased(cartItems)
        }

        val commonRes = commonResDeferred.await()
        val cartRes = cartResDeferred.await()

        Log.d(TAG, "Common recommends result: $commonRes")
        Log.d(TAG, "Cart-based recommends result: $cartRes")

        // Если хоть один – NoInternet → возвращаем NoInternet
        if (commonRes is Resource.ErrorNoInternet || cartRes is Resource.ErrorNoInternet) {
            Log.w(TAG, "One of the sources returned ErrorNoInternet")
            return@coroutineScope Resource.ErrorNoInternet()
        }

        // Получаем списки (успешные или пустые)
        val commonList = (commonRes as? Resource.Success)?.data.orEmpty()
        val cartList = (cartRes as? Resource.Success)?.data.orEmpty()

        Log.d(TAG, "Common list size: ${commonList.size}")
        Log.d(TAG, "Cart list size: ${cartList.size}")

        // ids элементов в корзине
        val inCartIds = cartItems.map { it.id }.toSet()
        Log.d(TAG, "Cart item IDs: $inCartIds")

        // Объединяем, удаляем дубликаты, фильтруем уже в корзине
        val merged = (cartList + commonList)
            .distinctBy { it.id }
            .filterNot { it.id in inCartIds }

        Log.d(TAG, "Merged recommends size: ${merged.size}, IDs: ${merged.map { it.id }}")

        return@coroutineScope if (merged.isEmpty()) {
            Log.w(TAG, "Merged list is empty, returning ErrorEmptyData")
            Resource.ErrorEmptyData()
        } else {
            Log.d(TAG, "Returning ${merged.size} recommendations")
            Resource.Success(merged)
        }
    }
}
