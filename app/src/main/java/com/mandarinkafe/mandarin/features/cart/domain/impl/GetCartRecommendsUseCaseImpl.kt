package com.mandarinkafe.mandarin.features.cart.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.domain.api.RecommendsSchemaRepository
import com.mandarinkafe.mandarin.features.cart.domain.model.RecommendsSchemaRule
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetCartRecommendsUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorEmptyData
import com.mandarinkafe.mandarin.util.Resource.ErrorNoInternet
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import com.mandarinkafe.mandarin.util.normalize

class GetCartRecommendsUseCaseImpl(
    private val schemaRepository: RecommendsSchemaRepository,
    private val menuCache: MenuCache,
) : GetCartRecommendsUseCase {
    override suspend fun invoke(cartItems: Set<Meal>): Resource<List<Meal>> {
        val schemaResult = schemaRepository.getRecommendsSchema()

        return when (schemaResult) {
            is ErrorEmptyData -> ErrorEmptyData()
            is ErrorNoInternet -> ErrorNoInternet()
            is ErrorOther -> ErrorOther(schemaResult.message.orEmpty())
            is Idle -> Idle<List<Meal>>()
            is Loading -> Loading()
            is Success -> {
                val rules = schemaResult.data ?: return ErrorEmptyData()

                val cartItemsWithNorm = normalizeCartItems(cartItems)

                val matchingRules = filterRules(rules, cartItemsWithNorm)

                val recommendedSkus = matchingRules
                    .flatMap { it.recommendedSku }
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                val meals = recommendedSkus
                    .flatMap { sku -> menuCache.getMealsBySku(sku) }
                    .distinctBy { it.id }

                Success(meals)
            }
        }
    }

    private fun normalizeCartItems(cartItems: Set<Meal>) =
        cartItems.map { meal ->
            meal.copy(
                name = meal.name.normalize(),
                categoryPath = meal.categoryPath.map { it.normalize() }
            )
        }

    private fun filterRules(rules: List<RecommendsSchemaRule>, cartItemsWithNorm: List<Meal>) =
        rules.filter { rule ->
            val rawRuleName = rule.sourceName
            if (rawRuleName.isBlank()) return@filter false
            val ruleName = rawRuleName.normalize()

            // Находим все элементы корзины, у которых совпало имя правила
            val matchingCartMeals = cartItemsWithNorm.filter { meal ->
                meal.name.equals(ruleName, ignoreCase = true) ||
                        meal.categoryPath.any { it.equals(ruleName, ignoreCase = true) }
            }

            if (matchingCartMeals.isEmpty()) {
                // Ничего не подходит по имени
                return@filter false
            }

            // Отбрасываем те, у которых SKU есть в excludeSku
            val allowedMeals = matchingCartMeals.filter { meal ->
                rule.excludeSku.none { ex -> meal.sku.equals(ex.trim(), ignoreCase = true) }
            }

            // Если после исключений остался хоть один — правило срабатывает
            allowedMeals.isNotEmpty()
        }
}
