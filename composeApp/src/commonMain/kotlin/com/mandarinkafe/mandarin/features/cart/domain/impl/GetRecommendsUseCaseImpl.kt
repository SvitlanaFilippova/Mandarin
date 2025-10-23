package com.mandarinkafe.mandarin.features.cart.domain.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.domain.api.RecommendsSchemaRepository
import com.mandarinkafe.mandarin.features.cart.domain.models.RecommendsSchemaRule
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetRecommendsUseCase
import com.mandarinkafe.mandarin.features.cart.domain.models.Recommends
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.ErrorEmptyData
import com.mandarinkafe.mandarin.util.Resource.ErrorNoInternet
import com.mandarinkafe.mandarin.util.Resource.ErrorOther
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success
import com.mandarinkafe.mandarin.util.normalize

class GetRecommendsUseCaseImpl(
    private val schemaRepository: RecommendsSchemaRepository,
    private val menuCache: MenuCache,
) : GetRecommendsUseCase {
    override suspend fun invoke(cartItems: Set<Meal>): Resource<Recommends> {
        val schemaResult = schemaRepository.getRecommendsSchema()

        return when (schemaResult) {
            is ErrorEmptyData -> ErrorEmptyData()
            is ErrorNoInternet -> ErrorNoInternet()
            is ErrorOther -> ErrorOther(schemaResult.message.orEmpty())
            is Idle -> Idle()
            is Loading -> Loading()
            is Success -> {
                val rules = schemaResult.data ?: return ErrorEmptyData()

                val cartItemsWithNorm = normalizeCartItems(cartItems)
                // ids элементов в корзине
                val inCartIds = cartItems.map { it.id }.toSet()

                // Сначала выделяем отдельные правила
                val separateRules = rules.filter { it.isSeparate }
                
                // Затем разделяем оставшиеся правила на глобальные и обычные
                val nonSeparateRules = rules.filter { !it.isSeparate }
                val (globalRules, normalRules) = nonSeparateRules.partition { it.sourceName == SOURCE_ALL }
                
                val matchingRules = filterRules(normalRules, cartItemsWithNorm)

                // Сначала обычные, потом глобальные
                val allMainRules = matchingRules + globalRules
                
                val recommendedSkus = allMainRules
                    .flatMap { it.recommendedSku }
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                val meals = recommendedSkus
                    .flatMap { sku -> menuCache.getMealsBySku(sku) }
                    .distinctBy { it.id }

                val matchingSeparateRules = filterRules(separateRules, cartItemsWithNorm)

                val separateSkus = matchingSeparateRules
                    .flatMap { it.recommendedSku }
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                val separateMeals = separateSkus
                    .flatMap { sku -> menuCache.getMealsBySku(sku) }
                    .distinctBy { it.id }
                    .filterNot { it.id in inCartIds }

                // фильтруем те блюда, что уже в корзине и что попали в отдельные
                val filtered = meals
                    .filterNot { it.id in inCartIds }
                    .filterNot { meal -> separateMeals.any { it.id == meal.id } }

                Success(Recommends(mainRecommends = filtered, separateRecommends = separateMeals))
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

    private fun filterRules(
        rules: List<RecommendsSchemaRule>,
        cartItemsWithNorm: List<Meal>
    ): List<RecommendsSchemaRule> =
        rules.filter { rule ->
            val rawRuleName = rule.sourceName
            if (rawRuleName.isBlank()) return@filter false
            val ruleName = rawRuleName.normalize()

            val matchingCartMeals = cartItemsWithNorm.filter { meal ->
                meal.name.equals(ruleName, ignoreCase = true) ||
                        meal.categoryPath.any { it.equals(ruleName, ignoreCase = true) }
            }

            if (matchingCartMeals.isEmpty()) return@filter false

            val allowedMeals = matchingCartMeals.filter { meal ->
                rule.excludeSku.none { ex -> meal.sku.equals(ex.trim(), ignoreCase = true) }
            }

            allowedMeals.isNotEmpty()
        }

    private companion object {
        const val SOURCE_ALL = "***"
    }
}
