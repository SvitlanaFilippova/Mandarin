package com.mandarinkafe.mandarin.menu.data.mapper

import android.util.Log
import com.mandarinkafe.mandarin.favorites.domain.api.FavoritesRepository
import com.mandarinkafe.mandarin.menu.data.dto.CategoryDto
import com.mandarinkafe.mandarin.menu.data.dto.LabelDto
import com.mandarinkafe.mandarin.menu.data.dto.MealDto
import com.mandarinkafe.mandarin.menu.data.dto.ModifierGroupDto
import com.mandarinkafe.mandarin.menu.data.dto.ModifierItemDto
import com.mandarinkafe.mandarin.menu.data.dto.TagDto
import com.mandarinkafe.mandarin.menu.domain.models.EditableType
import com.mandarinkafe.mandarin.menu.domain.models.Label
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.menu.domain.models.ModifierItem
import com.mandarinkafe.mandarin.menu.domain.models.Tag
import com.mandarinkafe.mandarin.util.Constants.TAG_PIZZA_ADDS
import com.mandarinkafe.mandarin.util.Constants.TAG_WOK_CONSTRUCTOR
import com.mandarinkafe.mandarin.util.applyTypography

class DtoToDomainConverter(favoritesRepository: FavoritesRepository) {
    private val storedFavorites = favoritesRepository.getFavoriteIds()

    fun menuDtoToDomain(menuDto: List<CategoryDto>?): List<MealCategory> {
        if (menuDto.isNullOrEmpty()) {
            Log.e("DEBUG", "menuDto оказался null или пустым")
            return emptyList()
        }

        val childCategoriesMap = groupSubcategories(menuDto)
        val topLevelCategories = menuDto.filter { !it.hasParent() }
        val topLevelNames = topLevelCategories.map { it.name }.toSet()

        val result = mutableListOf<MealCategory>()

        // 1. Обработка родительских категорий
        for (parent in topLevelCategories) {
            val subCategories = childCategoriesMap[parent.name]

            if (subCategories.isNullOrEmpty()) {
                // Нет подкатегорий — обычная категория с блюдами
                result.add(
                    parent.toDomain(
                        storedFavorites = storedFavorites
                    )
                )
            } else {
                // Есть подкатегории — собрать как категорию с subCategories
                result.add(buildParentCategory(parent, subCategories))
            }
        }

        // 2. Обработка случайных подкатегорий без родителя
        for (category in menuDto.filter { it.hasParent() }) {
            val parentName = category.parentName()
            if (!topLevelNames.contains(parentName)) {
                result.add(buildLonelySubcategory(category))
            }
        }

        return result
    }

    private fun CategoryDto.toDomain(storedFavorites: List<String>): MealCategory {
        val categoryLabels = labels?.map { it.toDomain() } ?: emptyList()
        val categoryTags = tags?.map { it.toDomain() } ?: emptyList()

        val safeItems = items?.mapNotNull {
            it.toDomain(
                storedFavorites = storedFavorites,
                categoryLabels = categoryLabels,
                categoryTags = categoryTags
            )
        } ?: emptyList()

        return MealCategory(
            id = id,
            name = name,
            meals = safeItems,
            subCategories = null,
            tabIcon = buttonImageUrl,
            description = (description ?: "").applyTypography(),
            isHidden = isHidden == true,
        )
    }

    private fun MealDto.toDomain(
        storedFavorites: List<String>,
        categoryLabels: List<Label>,
        categoryTags: List<Tag>
    ): Meal? {
        val firstSize = itemSizes?.firstOrNull()
        val safeWeight = firstSize?.portionWeightGrams?.toInt() ?: 0
        val safePrice = firstSize?.prices?.firstOrNull()?.price?.toInt() ?: 0
        val safeModifiers = firstSize?.itemModifierGroups?.map { it.toDomain() } ?: emptyList()
        val safeImageUrl = firstSize?.buttonImageUrl ?: ""

        val mealLabels = (labels ?: emptyList()).map { it.toDomain() }
        val mealTags = (tags ?: emptyList()).map { it.toDomain() }
        val finalMealTags = (mealTags + categoryTags).distinctBy { it.name }
        val finalMealLabels = (mealLabels + categoryLabels).distinctBy { it.name }

        return Meal(
            id = itemId,
            name = name.applyTypography(),
            description = (description ?: "").applyTypography(),
            weight = safeWeight,
            price = safePrice,
            imageUrl = safeImageUrl,
            isFavorite = storedFavorites.contains(itemId),
            labels = finalMealLabels,
            tags = finalMealTags,
            isHidden = isHidden == true,
            modifiers = safeModifiers,
            editableType = checkMealType(finalMealTags, safeModifiers),
        )
    }

    private fun checkMealType(tags: List<Tag>, modifiers: List<ModifierGroup>): EditableType? {
        if (tags.any { it.name.equals(TAG_PIZZA_ADDS, ignoreCase = true) }) {
            return EditableType.PIZZA
        } else
            if (tags.any { it.name.equals(TAG_WOK_CONSTRUCTOR, ignoreCase = true) }) {
                return EditableType.WOK
            } else if (modifiers.isNotEmpty()) {
                return EditableType.MODIFIABLE
            } else
                return null
    }

    private fun groupSubcategories(menuDto: List<CategoryDto>): Map<String, List<CategoryDto>> {
        return menuDto
            .filter { it.hasParent() }
            .groupBy { it.parentName() }
    }

    private fun buildParentCategory(
        parentDto: CategoryDto,
        subCategories: List<CategoryDto>?
    ): MealCategory {
        return MealCategory(
            id = parentDto.id,
            name = parentDto.name,
            meals = null,
            subCategories = subCategories?.map { subDto ->
                subDto.copy(name = subDto.subName()).toDomain(
                    storedFavorites = storedFavorites
                )
            },
            tabIcon = parentDto.buttonImageUrl,
            description = parentDto.description.orEmpty(),
            isHidden = parentDto.isHidden == true
        )
    }

    private fun buildLonelySubcategory(category: CategoryDto): MealCategory {
        Log.w("DEBUG", "Подкатегория '${category.name}' без родителя")
        return category.copy(name = category.subName()).toDomain(
            storedFavorites = storedFavorites
        )
    }

    fun CategoryDto.hasParent(): Boolean =
        name.contains("/")

    fun CategoryDto.parentName(): String =
        name.substringBefore("/")

    fun CategoryDto.subName(): String =
        name.substringAfter("/")

    fun TagDto.toDomain() = Tag(
        id = id,
        name = name
    )

    fun LabelDto.toDomain() = Label(
        id = code,
        name = name
    )

    fun ModifierItemDto.toDomain(): ModifierItem {

        val safeWeight = portionWeightGrams?.toInt() ?: 0
        val safePrice = prices?.firstOrNull()?.price?.toInt() ?: 0

        return ModifierItem(
            id = itemId ?: "",
            name = name ?: "",
            sku = sku ?: "",
            isHidden = isHidden == true,
            weight = safeWeight,
            price = safePrice
        )
    }

    fun ModifierGroupDto.toDomain() = ModifierGroup(
        id = itemGroupId ?: "",
        name = name ?: "",
        sku = sku ?: "",
        items = items?.map { it.toDomain() } ?: emptyList()
    )

}