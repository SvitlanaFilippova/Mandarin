package com.mandarinkafe.mandarin.features.menu.data.impl

import com.mandarinkafe.mandarin.core.data.api.MenuFetcher
import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.features.menu.data.dto.CategoryDto
import com.mandarinkafe.mandarin.features.menu.data.dto.MenuResponse
import com.mandarinkafe.mandarin.features.menu.data.mapper.toDomain
import com.mandarinkafe.mandarin.features.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.util.Constants.CATEGORY_ADDS
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.applyTypography
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.UUID

@Singleton
class MenuRepositoryImpl @Inject constructor(
    private val iikoNetworkClient: IikoNetworkClient,
) : MenuRepository, MenuFetcher {

    override suspend fun fetchMenu(): Resource<List<MealCategory>> {
        return try {
            val response = iikoNetworkClient.getMenu()

            when (response.resultCode) {
                NO_CONNECTION -> Resource.ErrorNoInternet()
                HTTP_SUCCESS -> {
                    val categories = (response as MenuResponse).itemCategories
                    if (!categories.isNullOrEmpty()) {
                        Resource.Success(buildMenuStructure(categories))
                    } else {
                        Resource.ErrorEmptyData()
                    }
                }

                else -> Resource.ErrorOther("Ошибка сервера или пустой ответ")
            }
        } catch (e: Exception) {
            Resource.ErrorOther("Ошибка: ${e.message}")
        }
    }

    private fun buildMenuStructure(menuDto: List<CategoryDto>): List<MealCategory> {
        val addonPaths = collectAddonPaths(menuDto)
        val map = buildBuilderMap(menuDto)
        linkBuilderHierarchy(map)

        return map
            .filterKeys { it.size == 1 } // Только корневые категории
            .values
            .mapNotNull { builder -> toMealCategory(builder, addonPaths) }
    }

    private fun collectAddonPaths(dtoList: List<CategoryDto>): List<List<String>> {
        return dtoList
            .map { it.name.split('/').map(String::trim) }
            .filter { CATEGORY_ADDS in it }
    }

    private fun buildBuilderMap(dtoList: List<CategoryDto>): MutableMap<List<String>, Builder> {
        val map = mutableMapOf<List<String>, Builder>()

        for (dto in dtoList) {
            val path = dto.name.split('/').map { it.trim() }

            for (len in 1..path.size) {
                val prefix = path.take(len)
                val isLeaf = len == path.size

                val builder = map.getOrPut(prefix) {
                    Builder(
                        dto = if (isLeaf) dto else null,
                        name = prefix.last(),
                        fullPath = prefix
                    )
                }
                if (isLeaf) {
                    builder.dto = dto
                }
            }
        }
        return map
    }

    private fun linkBuilderHierarchy(map: Map<List<String>, Builder>) {
        for ((path, builder) in map) {
            if (path.size > 1) {
                val parentPath = path.dropLast(1)
                val parent = map[parentPath]
                parent?.children?.add(builder)
            }
        }
    }

    private fun toMealCategory(builder: Builder, addonPaths: List<List<String>>): MealCategory? {
        val dto = builder.dto

        val meals = dto?.items?.mapNotNull { mealDto ->
            val isAddable = isAddableForPath(builder.fullPath, addonPaths)
            mealDto.toDomain(
                categoryLabels = dto.labels?.map { it.toDomain() } ?: emptyList(),
                categoryTags = dto.tags?.map { it.toDomain() } ?: emptyList(),
                categoryPath = builder.fullPath,
                isAddable = isAddable
            )
        }

        val subCategories = builder.children.mapNotNull { toMealCategory(it, addonPaths) }

        if ((meals == null || meals.isEmpty()) && builder.children.isEmpty()) {
            return null
        }
        return MealCategory(
            id = dto?.id ?: UUID.nameUUIDFromBytes(builder.fullPath.joinToString("/").toByteArray())
                .toString(),
            name = builder.name,
            meals = meals,
            subCategories = subCategories,
            tabIcon = dto?.buttonImageUrl,
            description = dto?.description.orEmpty().applyTypography(),
            isHidden = dto?.isHidden == true,
            categoryPath = builder.fullPath
        )
    }

    private fun isAddableForPath(
        categoryPath: List<String>,
        addonPaths: List<List<String>>
    ): Boolean {
        if (categoryPath.isEmpty()) return false
        val mainCategory = categoryPath.firstOrNull() ?: return false
        val isAddable =
            addonPaths.any { it.getOrNull(0) == mainCategory && it.getOrNull(1) == CATEGORY_ADDS }
        return isAddable
    }

    private class Builder(
        var dto: CategoryDto?,
        val name: String,
        val fullPath: List<String>,
        val children: MutableList<Builder> = mutableListOf()
    )
}