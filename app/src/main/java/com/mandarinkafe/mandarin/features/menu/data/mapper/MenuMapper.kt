package com.mandarinkafe.mandarin.features.menu.data.mapper

import com.mandarinkafe.mandarin.core.domain.models.Label
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.domain.models.Tag
import com.mandarinkafe.mandarin.features.menu.data.dto.CategoryDto
import com.mandarinkafe.mandarin.features.menu.data.dto.LabelDto
import com.mandarinkafe.mandarin.features.menu.data.dto.MealDto
import com.mandarinkafe.mandarin.features.menu.data.dto.ModifierGroupDto
import com.mandarinkafe.mandarin.features.menu.data.dto.ModifierItemDto
import com.mandarinkafe.mandarin.features.menu.data.dto.TagDto
import com.mandarinkafe.mandarin.util.Constants.TAG_ADDS
import com.mandarinkafe.mandarin.util.Constants.TAG_NO_DISCOUNT
import com.mandarinkafe.mandarin.util.applyTypography
import com.mandarinkafe.mandarin.util.removeLeadingDash

fun CategoryDto.toDomain(
    storedFavorites: List<String>,
    topCategoryName: String? = null
): MealCategory {
    val categoryLabels = labels?.map { it.toDomain() } ?: emptyList()
    val categoryTags = tags?.map { it.toDomain() } ?: emptyList()

    val nameForMeal = if (!topCategoryName.isNullOrEmpty()) "$topCategoryName / $name" else name

    val safeItems = items?.mapNotNull {
        it.toDomain(
            storedFavorites = storedFavorites,
            categoryLabels = categoryLabels,
            categoryTags = categoryTags,
            parentCategoryName = nameForMeal
        )
    } ?: emptyList()

    return MealCategory(
        id = id,
        name = name.applyTypography(),
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
    categoryTags: List<Tag>,
    parentCategoryName: String
): Meal? {
    val firstSize = itemSizes?.firstOrNull()
    val safeWeight = firstSize?.portionWeightGrams?.toInt() ?: 0
    val safePrice = firstSize?.prices?.firstOrNull()?.price?.toInt() ?: 0
    val safeImageUrl = firstSize?.buttonImageUrl ?: ""
    val safeModifiers = firstSize
        ?.itemModifierGroups
        ?.map { it.toDomain() }
        ?.sortedByDescending { it.isSingleChoice } // сначала выводим SingleChoice-модификаторы
        ?: emptyList()

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
        isAddable = finalMealTags.any { it.name.equals(TAG_ADDS, ignoreCase = true) },
        requireSelection = safeModifiers.any { it.isRequired },
        isModifiable = safeModifiers.isNotEmpty() && safePrice > 0,
        discountable = !finalMealTags.any { it.name.equals(TAG_NO_DISCOUNT, ignoreCase = true) },
        parentCategoryName = parentCategoryName,
    )
}

fun CategoryDto.hasParent(): Boolean =
    name.contains("/")

fun CategoryDto.parentName(): String =
    name.substringBefore("/")
        .trim()

fun CategoryDto.subName(): String =
    name.substringAfter("/")
        .trim()

fun TagDto.toDomain() = Tag(
    id = id,
    name = name
)

fun LabelDto.toDomain() = Label(
    id = code,
    name = name
)

fun ModifierItemDto.toDomain(): ModifierItem {
    val safePrice = prices?.firstOrNull()?.price?.toInt() ?: 0

    return ModifierItem(
        id = itemId,
        name = name?.removeLeadingDash()?.applyTypography() ?: "",
        price = safePrice,
    )
}

fun ModifierGroupDto.toDomain() = ModifierGroup(
    id = itemGroupId,
    name = name ?: "",
    items = items
        ?.map { it.toDomain() }
        ?.sortedBy { it.price != 0 }
        ?: emptyList(),
    isSingleChoice = (restrictions?.maxQuantity == 1),
    isRequired = (restrictions?.minQuantity ?: 0) > 0,
    maxQuantity = restrictions?.maxQuantity ?: Int.MAX_VALUE
)
