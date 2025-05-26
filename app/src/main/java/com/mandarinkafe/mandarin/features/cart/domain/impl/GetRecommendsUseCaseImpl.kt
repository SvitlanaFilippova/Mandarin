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
        // 1. Получаем правила из репозитория
        val schemaResult = schemaRepository.getRecommendsSchema()
        if (schemaResult !is Resource.Success) return emptySet()
        val rules = schemaResult.data ?: return emptySet()

        // 2. Нормализуем названия из корзины сразу в поле Meal
        val cartItemsWithNorm = cartItems.map { meal ->
            meal.copy(  // копируем, чтобы удобнее хранить нормализованные имена
                name = meal.name.normalize(),
                parentCategoryName = meal.parentCategoryName.normalize(),
                grandParentCategoryName = meal.grandParentCategoryName?.normalize().orEmpty()
            )
        }

        // 3. Фильтруем правила
        val matchingRules = rules.filter { rule ->
            val rawRuleName = rule.sourceName
            if (rawRuleName.isBlank()) return@filter false
            val ruleName = rawRuleName.normalize()

            // 3.1. Находим все элементы корзины, у которых совпало имя правила
            val matchingCartMeals = cartItemsWithNorm.filter { meal ->
                listOfNotNull(meal.name, meal.parentCategoryName, meal.grandParentCategoryName)
                    .any { it.equals(ruleName, ignoreCase = true) }
            }

            if (matchingCartMeals.isEmpty()) {
                // Ничто не подходит по имени
                return@filter false
            }

            // 3.2. Отбрасываем те, у которых SKU есть в excludeSku
            val allowedMeals = matchingCartMeals.filter { meal ->
                rule.excludeSku.none { ex -> meal.sku.equals(ex.trim(), ignoreCase = true) }
            }

            // Если после исключений остался хоть один — правило годится
            allowedMeals.isNotEmpty()
        }

        // 4. Собираем сет рекомендованных артикулов
        val recommendedSkus: Set<String> = matchingRules
            .flatMap { it.recommendedSku }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

        // 5. По каждому артикулу получаем блюда и убираем дубликаты по id
        return recommendedSkus
            .flatMap { sku -> menuRepository.getMealsBySku(sku) }
            .distinctBy { it.id }
            .toSet()
    }
}
