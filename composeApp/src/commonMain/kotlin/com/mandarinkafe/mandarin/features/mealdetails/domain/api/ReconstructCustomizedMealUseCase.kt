package com.mandarinkafe.mandarin.features.mealdetails.domain.api

import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.util.Resource

interface ReconstructCustomizedMealUseCase {
    suspend operator fun invoke(
        mealId: String,
        addsIds: List<String>,
        modifierIds: Map<String, List<String>>, // groupId -> list of itemIds
        comment: String = "",
        cartItemId: String? = null,
    ): Resource<CartItem>
}

