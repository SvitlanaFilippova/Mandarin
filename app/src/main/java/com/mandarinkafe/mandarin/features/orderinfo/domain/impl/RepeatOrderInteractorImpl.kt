package com.mandarinkafe.mandarin.features.orderinfo.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.features.menu.domain.mappers.toMealAdditional
import com.mandarinkafe.mandarin.features.orderinfo.data.toCartItem
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.RepeatOrderInteractor
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingOrderItem
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.RepeatOrderResult
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.first

class RepeatOrderInteractorImpl(
    private val menuCache: MenuCache
) : RepeatOrderInteractor {

    override suspend fun mapToCartItems(incoming: List<IncomingOrderItem>): RepeatOrderResult {
        menuCache.visibleMenu.first { it is Resource.Success }

        val validItems = mutableListOf<CartItem>()
        var invalidFound = false

        for (item in incoming) {
            val baseMeal = menuCache.getMealById(item.id)
            if (baseMeal == null) {
                invalidFound = true
                continue
            }

            val validAdds = item.chosenAdds.mapNotNull { add ->
                menuCache.getMealById(add.id)?.toMealAdditional()
            }.also { if (it.size < item.chosenAdds.size) invalidFound = true }

            val validMods =
                item.chosenModifiers.groupBy { it.groupId }.mapNotNull { (groupId, incomingMods) ->
                    val referenceGroup = baseMeal.modifiers.find { it.id == groupId }
                    referenceGroup?.copy(
                        items = incomingMods.mapNotNull { m -> referenceGroup.items.find { it.id == m.id } }
                    )?.takeIf { it.items.isNotEmpty() }
                }.also {
                    if (it.size < item.chosenModifiers.distinctBy { it.id }.size) invalidFound =
                        true
                }

            validItems += item.toCartItem(
                baseMeal = baseMeal,
                adds = validAdds,
                modifiers = validMods
            )
        }

        return RepeatOrderResult(
            cartItems = validItems,
            hasInvalidItems = invalidFound
        )
    }
}