package com.mandarinkafe.mandarin.shared.cart.domain.usecase

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.util.Resource

/**
 * Собирает рекомендации из точечных, которые зависят от состава корзины, и общих и склеивает их в один список
 */
interface GetAllRecommendsUseCase {

    suspend operator fun invoke(cartItems: Set<Meal>): Resource<List<Meal>>
}