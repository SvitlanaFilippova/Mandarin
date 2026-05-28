package com.mandarinkafe.mandarin.features.orderinfo.data.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.orderinfo.data.toDomain
import com.mandarinkafe.mandarin.features.orderinfo.domain.api.OrderInfoRepository
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingModifier
import com.mandarinkafe.mandarin.features.ordershistory.data.mapper.OrdersHistoryMapper.toOrderInfoResponseDto
import com.mandarinkafe.mandarin.features.ordershistory.data.network.OrdersHistoryServerApi
import com.mandarinkafe.mandarin.util.Constants.BEARER_TOKEN_TYPE
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay

class OrderInfoRepositoryImpl(
    private val serverApi: OrdersHistoryServerApi,
    private val menuCache: MenuCache,
    private val authRepository: AuthRepository,
) : OrderInfoRepository {

    private companion object {
        private const val FIRST_RETRY_DELAY_MS = 1000L
        private const val SECOND_RETRY_DELAY_MS = 2000L
        private const val ERROR_EMPTY_SERVER_RESPONSE = "Ошибка сервера или пустой ответ"

        fun buildAuthToken(token: String) = "$BEARER_TOKEN_TYPE $token"
    }

    override suspend fun getOrderFromApi(id: String): Resource<IncomingOrder> {
        // Сначала пытаемся получить с сервера
        val serverResult = tryGetOrderFromServer(id)
        if (serverResult != null) {
            return serverResult
        }

        // Если сервер вернул 404, делаем повторную попытку через 1 секунду
        // Это нужно, потому что при создании заказа сервер может еще не успеть обработать заказ
        delay(FIRST_RETRY_DELAY_MS)
        val retryServerResult = tryGetOrderFromServer(id)
        if (retryServerResult != null) {
            return retryServerResult
        }

        // Если и вторая попытка не удалась, делаем третью попытку через еще 2 секунды
        delay(SECOND_RETRY_DELAY_MS)
        val thirdRetryServerResult = tryGetOrderFromServer(id)
        if (thirdRetryServerResult != null) {
            return thirdRetryServerResult
        }

        // Если детали ещё не успели сохраниться из webhook/create, просим сервер сам сходить в iiko.
        return getOrderFromIiko(id)
    }

    private suspend fun tryGetOrderFromServer(id: String): Resource<IncomingOrder>? {
        val token = authRepository.getAccessToken() ?: return null

        return try {
            val response = serverApi.getOrderDetails(buildAuthToken(token), id)

            when (response.resultCode) {
                HTTP_SUCCESS -> {
                    val addons = menuCache.addonsCategories.value
                    val orderInfoDto = response.toOrderInfoResponseDto()
                    val orderInfo = orderInfoDto.toDomain(addons)

                    run {
                        val validatedOrder = validateOrderItemsWithMenu(order = orderInfo)
                        Resource.Success(data = validatedOrder)
                    }
                }

                HttpStatusCode.NotFound.value -> {
                    // Заказ не найден на сервере (404) - это нормально для только что созданных заказов
                    // Делаем fallback на iiko
                    null
                }

                else -> {
                    // Сервер вернул другую ошибку (500, и т.д.) - делаем fallback на iiko
                    null
                }
            }
        } catch (e: Exception) {
            Napier.e("OrderInfoRepositoryImpl, tryGetOrderFromServer error: $e", e)
            null // Ошибка при запросе к серверу - делаем fallback на iiko
        }
    }

    override suspend fun getOrderFromIiko(id: String): Resource<IncomingOrder> {
        val token = authRepository.getAccessToken()
            ?: return Resource.ErrorOther("Токен авторизации не найден")

        return try {
            val response = serverApi.getOrderStatus(buildAuthToken(token), id)
            when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet()
                HTTP_SUCCESS -> {
                    val addons = menuCache.addonsCategories.value
                    val orderInfo = response.orders
                        .firstOrNull { it.id == id }
                        ?.toDomain(addons)

                    if (orderInfo != null) {
                        val validatedOrder = validateOrderItemsWithMenu(order = orderInfo)
                        Resource.Success(data = validatedOrder)
                    } else {
                        Resource.ErrorOther(ERROR_EMPTY_SERVER_RESPONSE)
                    }
                }

                HttpStatusCode.NotFound.value -> Resource.ErrorOther("Заказ не найден")
                else -> Resource.ErrorOther(ERROR_EMPTY_SERVER_RESPONSE)
            }
        } catch (e: Exception) {
            Napier.e("OrderInfoRepositoryImpl, getOrderFromIiko(server) error: $e", e)
            Resource.ErrorOther(ERROR_EMPTY_SERVER_RESPONSE)
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
