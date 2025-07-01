package com.mandarinkafe.mandarin.features.cart.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.domain.api.RecommendsSchemaRepository
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetRecommendsUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorEmptyData
import com.mandarinkafe.mandarin.util.Resource.ErrorNoInternet
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.normalize

class GetRecommendsUseCaseImpl(
    private val schemaRepository: RecommendsSchemaRepository,
    private val menuCache: MenuCache,
) :
    GetRecommendsUseCase {
    override suspend fun invoke(cartItems: Set<Meal>): Resource<List<Meal>> {
        // 1. Получаем правила из репозитория
        val schemaResult = schemaRepository.getRecommendsSchema()
        return when (schemaResult) {
            is ErrorEmptyData -> ErrorEmptyData()
            is ErrorNoInternet -> ErrorNoInternet()
            is ErrorOther -> ErrorOther(schemaResult.message.orEmpty())
            is Idle -> Idle<List<Meal>>()
            is Loading -> Loading()
            is Resource.Success<*> -> {
                val rules = schemaResult.data ?: return ErrorEmptyData()

                // 2. Нормализуем названия блюд и их категорий из корзины
                val cartItemsWithNorm = cartItems.map { meal ->
                    meal.copy(
                        name = meal.name.normalize(),
                        parentCategoryName = meal.parentCategoryName.normalize(),
                        grandParentCategoryName = meal.grandParentCategoryName?.normalize()
                            .orEmpty()
                    )
                }

                // 3. Фильтруем правила
                val matchingRules = rules.filter { rule ->
                    val rawRuleName = rule.sourceName
                    if (rawRuleName.isBlank()) return@filter false
                    val ruleName = rawRuleName.normalize()

                    // 3.1. Находим все элементы корзины, у которых совпало имя правила
                    val matchingCartMeals = cartItemsWithNorm.filter { meal ->
                        listOfNotNull(
                            meal.name,
                            meal.parentCategoryName,
                            meal.grandParentCategoryName
                        )
                            .any { it.equals(ruleName, ignoreCase = true) }
                    }

                    if (matchingCartMeals.isEmpty()) {
                        // Ничего не подходит по имени
                        return@filter false
                    }

                    // 3.2. Отбрасываем те, у которых SKU есть в excludeSku
                    val allowedMeals = matchingCartMeals.filter { meal ->
                        rule.excludeSku.none { ex -> meal.sku.equals(ex.trim(), ignoreCase = true) }
                    }

                    // Если после исключений остался хоть один — правило срабатывает
                    allowedMeals.isNotEmpty()
                }

                // 4. Собираем сет рекомендованных артикулов
                val recommendedSkus: List<String> = matchingRules
                    .flatMap { it.recommendedSku }
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                // 5. По каждому артикулу получаем блюда и убираем дубликаты по id
                return Resource.Success(
                    recommendedSkus
                        .flatMap { sku -> menuCache.getMealsBySku(sku) }
                        .distinctBy { it.id }
                )
            }
        }
    }
}
