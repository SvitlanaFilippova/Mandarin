package com.mandarinkafe.mandarin.features.search.data.impl

import com.mandarinkafe.mandarin.core.domain.api.MenuCache
import com.mandarinkafe.mandarin.core.domain.models.Label
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.search.domain.api.LabelsRepository
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.first

class LabelsRepositoryImpl(
    private val menuCache: MenuCache,
) : LabelsRepository {

    private var cachedLabels: List<Label>? = null
    private var lastMenuHash: Int? = null

    override suspend fun getLabels(): List<Label> {
        // Ждём, пока меню загрузится
        menuCache.mainMenu.first { it is Resource.Success }

        val currentMenu =
            (menuCache.mainMenu.value as? Resource.Success)?.data ?: return emptyList()

        val currentHash = currentMenu.hashCode()

        // Если меню не менялось — возвращаем кэш
        if (currentHash == lastMenuHash && cachedLabels != null) {
            return cachedLabels!!
        }

        val labelCounts = mutableMapOf<Label, Int>()

        fun collectLabels(category: MealCategory) {
            category.meals?.forEach { meal ->
                meal.labels.forEach { label ->
                    labelCounts[label] = (labelCounts[label] ?: 0) + 1
                }
            }
            category.subCategories?.forEach { subCategory ->
                collectLabels(subCategory)
            }
        }

        currentMenu.forEach { collectLabels(it) }

        // Сортируем по убыванию количества повторений ярлыка
        cachedLabels = labelCounts.entries
            .sortedByDescending { it.value }
            .map { it.key }

        lastMenuHash = currentHash

        return cachedLabels.orEmpty()
    }
}
