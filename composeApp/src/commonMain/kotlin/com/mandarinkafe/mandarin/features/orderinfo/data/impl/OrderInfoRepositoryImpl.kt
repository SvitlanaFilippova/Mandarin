package com.mandarinkafe.mandarin.features.orderinfo.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.orderinfo.data.network.OrdersInfoResponse
import com.mandarinkafe.mandarin.features.orderinfo.data.toDomain
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.OrderInfoRepository
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingModifier
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class OrderInfoRepositoryImpl(
    private val networkClient: IikoNetworkClient,
    private val menuCache: MenuCache,
) : OrderInfoRepository {

    override suspend fun getOrderFromApi(id: String): Resource<IncomingOrder> {
        val response = networkClient.getSingleOrderInfoById(id)
        return when (response.resultCode) {
            NO_CONNECTION -> Resource.ErrorNoInternet()
            HTTP_SUCCESS -> {
                val addons = menuCache.addonsCategories.value
                val orderInfo = (response as OrdersInfoResponse)
                    .orders
                    .firstOrNull { it.id == id }
                    ?.toDomain(addons)

                if (orderInfo != null) {
                    val validatedOrder = validateOrderItemsWithMenu(order = orderInfo)
                    Resource.Success(data = validatedOrder)
                } else {
                    Resource.ErrorOther(
                        "Ошибка сервера или пустой ответ"
                    )
                }
            }

            else -> Resource.ErrorOther("Ошибка сервера или пустой ответ")
        }
    }

    private fun validateOrderItemsWithMenu(
        order: IncomingOrder,
    ): IncomingOrder {
        val validatedItems = order.items.map { item ->
            val meal = menuCache.getMealById(item.id)

            val validatedAdds = item.chosenAdds.map { add ->
                val addMeal = menuCache.getMealById(add.id)
                if (addMeal != null) {
                    add.copy(
                        name = addMeal.name,
                        isValidated = true
                    )
                } else {
                    add
                }
            }
            val validatedModifiers = if (meal != null) {
                item.chosenModifiers.updateNamesFrom(meal.modifiers)
            } else {
                item.chosenModifiers // оставляем как есть
            }

            if (meal != null) {
                item.copy(
                    name = meal.name,
                    isValidated = true,
                    chosenAdds = validatedAdds,
                    chosenModifiers = validatedModifiers
                )
            } else {
                item.copy(
                    chosenAdds = validatedAdds,
                    chosenModifiers = validatedModifiers
                )
            }
        }

        return order.copy(items = validatedItems)
    }

    private fun List<IncomingModifier>.updateNamesFrom(mealModifiers: List<ModifierGroup>): List<IncomingModifier> {
        return this.map { incomingModifier ->
            // Ищем группу модификаторов, к которой относится этот модификатор
            val referenceGroup = mealModifiers.find { it.id == incomingModifier.groupId }
            if (referenceGroup != null) {
                // Ищем конкретный модификатор в группе
                val referenceItem = referenceGroup.items.find { it.id == incomingModifier.id }
                if (referenceItem != null) {
                    // Обновляем названия модификатора и группы
                    incomingModifier.copy(
                        name = referenceItem.name,
                        groupName = referenceGroup.name
                    )
                } else {
                    // Модификатор не найден в группе, но группа найдена - обновляем только название группы
                    incomingModifier.copy(
                        groupName = referenceGroup.name
                    )
                }
            } else {
                // Группа не найдена - оставляем как есть
                incomingModifier
            }
        }
    }
}