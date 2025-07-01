package com.mandarinkafe.mandarin.features.menu.data.mapper

import com.mandarinkafe.mandarin.core.domain.models.Label
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.domain.models.Tag
import com.mandarinkafe.mandarin.features.menu.data.dto.BannerDto
import com.mandarinkafe.mandarin.features.menu.data.dto.CategoryDto
import com.mandarinkafe.mandarin.features.menu.data.dto.ItemSize
import com.mandarinkafe.mandarin.features.menu.data.dto.LabelDto
import com.mandarinkafe.mandarin.features.menu.data.dto.MealDto
import com.mandarinkafe.mandarin.features.menu.data.dto.ModifierGroupDto
import com.mandarinkafe.mandarin.features.menu.data.dto.ModifierItemDto
import com.mandarinkafe.mandarin.features.menu.data.dto.TagDto
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.util.Constants.TAG_ADDS
import com.mandarinkafe.mandarin.util.Constants.TAG_NO_ADDS
import com.mandarinkafe.mandarin.util.Constants.TAG_NO_DISCOUNT
import com.mandarinkafe.mandarin.util.applyTypography
import com.mandarinkafe.mandarin.util.removeLeadingDash

fun CategoryDto.toDomain(
    topCategoryName: String? = null
): MealCategory {
    val categoryLabels = labels?.map { it.toDomain() } ?: emptyList()
    val categoryTags = tags?.map { it.toDomain() } ?: emptyList()

    val safeItems = items?.mapNotNull {
        it.toDomain(
            categoryLabels = categoryLabels,
            categoryTags = categoryTags,
            parentCategoryName = name,
            grandParentCategoryName = topCategoryName
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
    categoryLabels: List<Label>,
    categoryTags: List<Tag>,
    parentCategoryName: String,
    grandParentCategoryName: String?
): Meal? {
    val firstSize = itemSizes?.firstOrNull()
    val safeWeight = firstSize?.portionWeightGrams?.toInt() ?: 0
    val safePrice = firstSize?.prices?.firstOrNull()?.price?.toInt() ?: 0
    val safeImageUrl = firstSize?.buttonImageUrl ?: ""
    val safeModifiers = getSafeModifiers(firstSize)

    val mealLabels = (labels ?: emptyList()).map { it.toDomain() }
    val mealTags = (tags ?: emptyList()).map { it.toDomain() }

    val finalMealTags = mergeTags(mealTags, categoryTags)
    val finalMealLabels = mergeLabels(mealLabels, categoryLabels)

    val hasNoAddsTag = finalMealTags.any { it.name.equals(TAG_NO_ADDS, ignoreCase = true) }
    val isRequireSelection = safeModifiers.any { it.isRequired }

    return Meal(
        id = itemId,
        name = name.applyTypography(),
        description = (description ?: "").applyTypography(),
        sku = sku ?: "",
        weight = safeWeight,
        price = safePrice,
        imageUrl = safeImageUrl,
        labels = finalMealLabels,
        tags = finalMealTags,
        isHidden = isHidden == true,
        modifiers = safeModifiers,
        isAddable = !hasNoAddsTag && finalMealTags.any {
            it.name.equals(
                TAG_ADDS,
                ignoreCase = true
            )
        },
        requireSelection = isRequireSelection,
        isModifiable = safeModifiers.isNotEmpty() && safePrice > 0 && !isRequireSelection,
        discountable = !finalMealTags.any { it.name.equals(TAG_NO_DISCOUNT, ignoreCase = true) },
        parentCategoryName = parentCategoryName,
        grandParentCategoryName = grandParentCategoryName,
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

fun ModifierGroupDto.toDomain(): ModifierGroup {
    return ModifierGroup(
        id = itemGroupId,
        name = name ?: "",
        items = items
            ?.map { it.toDomain() }
            ?.sortedBy { it.price != 0 }
            ?: emptyList(),
        isSingleChoice = restrictions?.maxQuantity == 1,
        isRequired = restrictions?.minQuantity?.let { it > 0 } == true,
        maxQuantity = restrictions?.maxQuantity ?: Int.MAX_VALUE
    )

}

fun BannerDto.toDomain() = Banner(
    imageUrl = imageUrl ?: "",
    targetName = targetName ?: "",
)

private fun getSafeModifiers(firstSize: ItemSize?) = firstSize
    ?.itemModifierGroups
    ?.map { it.toDomain() }
    ?.sortedByDescending { it.isSingleChoice }
    ?: emptyList()

private fun mergeTags(mealTags: List<Tag>, categoryTags: List<Tag>): List<Tag> {
    val hasNoAddsTag = mealTags.any { it.name.equals(TAG_NO_ADDS, ignoreCase = true) }
    return if (hasNoAddsTag) {
        mealTags
    } else {
        (mealTags + categoryTags).distinctBy { it.name }
    }
}

private fun mergeLabels(mealLabels: List<Label>, categoryLabels: List<Label>): List<Label> {
    return (mealLabels + categoryLabels).distinctBy { it.name }
}
