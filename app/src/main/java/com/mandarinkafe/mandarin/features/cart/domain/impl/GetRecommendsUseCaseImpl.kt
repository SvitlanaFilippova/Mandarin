package com.mandarinkafe.mandarin.features.cart.domain.impl

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.cart.domain.api.RecommendsSchemaRepository
import com.mandarinkafe.mandarin.features.cart.domain.usecase.GetRecommendsUseCase
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.normalize

class GetRecommendsUseCaseImpl(
    private val schemaRepository: RecommendsSchemaRepository,
    private val menuRepository: MenuRepository,
) :
    GetRecommendsUseCase {
    override suspend fun invoke(cartItems: Set<Meal>): Set<Meal> {
        // Получаем правила из репозитория
        val schemaResult = schemaRepository.getRecommendsSchema()
        if (schemaResult !is Resource.Success) return emptySet()
        val rules = schemaResult.data ?: return emptySet()

        // Собираем сеты для быстрого поиска
        val cartSkus: Set<String> = cartItems.map { it.sku }.toSet()
        val cartNames: Set<String> = cartItems
            .flatMap { listOfNotNull(it.name, it.parentCategoryName, it.grandParentCategoryName) }
            .map { raw ->
                val norm = raw.normalize()
                norm
            }
            .toSet()

        //  Фильтруем правила по названию (sourceName) и исключениям
        val matchingRules = rules.filter { rule ->
            val rawRuleName = rule.sourceName
            if (rawRuleName.isEmpty()) return@filter false

            // Нормализуем имя правила
            val ruleName = rawRuleName.normalize()

            // Проверяем исключения по SKU
            val isExcluded = rule.excludeSku
                .any { exclude -> cartSkus.any { it.equals(exclude, ignoreCase = true) } }
            if (isExcluded) {
                return@filter false
            }

            // Правило срабатывает, если имя правила совпадает с любым именем из корзины
            val nameMatches = cartNames.any { cartName ->
                val match = cartName.equals(ruleName, ignoreCase = true)
                match
            }
            nameMatches
        }

        //  Собираем сет рекомендованных артикулов
        val recommendedSkus: Set<String> = matchingRules
            .flatMap { rule ->

                rule.recommendedSku
            }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

//  По каждому рекомендованному артикулу получаем блюда и убираем дубли по id
        return recommendedSkus
            .flatMap { sku -> menuRepository.getMealsBySku(sku) }
            .distinctBy { it.id }
            .toSet()

    }
}

