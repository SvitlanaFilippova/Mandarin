package com.mandarinkafe.mandarin.features.menu.data.mapper

import com.mandarinkafe.mandarin.core.domain.models.Label
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.core.domain.models.MeasureUnitType
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.domain.models.Tag
import com.mandarinkafe.mandarin.features.menu.data.dto.BannerDto
import com.mandarinkafe.mandarin.features.menu.data.dto.CategoryDto
import com.mandarinkafe.mandarin.features.menu.data.dto.ItemSizeDTO
import com.mandarinkafe.mandarin.features.menu.data.dto.LabelDto
import com.mandarinkafe.mandarin.features.menu.data.dto.MealDto
import com.mandarinkafe.mandarin.features.menu.data.dto.ModifierGroupDto
import com.mandarinkafe.mandarin.features.menu.data.dto.ModifierItemDto
import com.mandarinkafe.mandarin.features.menu.data.dto.TagDto
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.util.Constants.TAG_ADDS
import com.mandarinkafe.mandarin.util.Constants.TAG_NO_ADDS
import com.mandarinkafe.mandarin.util.Constants.TAG_NO_DELIVERY
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
    val firstSize = itemSizes?.firstOrNull() ?: return null

    val baseInfo = extractBaseInfo(firstSize)
    val safeModifiers = getSafeModifiers(firstSize)

    val mealLabels = (labels ?: emptyList()).map { it.toDomain() }
    val mealTags = (tags ?: emptyList()).map { it.toDomain() }

    val finalMealLabels = mergeLabels(mealLabels, categoryLabels)
    val finalMealTags = mergeTags(mealTags, categoryTags)

    return Meal(
        id = itemId,
        name = name.applyTypography(),
        description = (description ?: "").applyTypography(),
        sku = sku ?: "",
        weight = baseInfo.weight,
        measureUnitType = MeasureUnitType.from(firstSize.measureUnitType) ?: MeasureUnitType.GRAM,
        price = baseInfo.price,
        imageUrl = baseInfo.imageUrl ?: "",
        labels = finalMealLabels,
        tags = finalMealTags,
        isHidden = isHidden == true,
        modifiers = safeModifiers,
        isAddable = isAddable(finalMealTags),
        requireSelection = requireSelection(safeModifiers),
        isModifiable = isModifiable(safeModifiers, baseInfo.price),
        isPickupOnly = finalMealTags.any { it.name.equals(TAG_NO_DELIVERY, ignoreCase = true) },
        discountable = isDiscountable(finalMealTags),
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
    val isSingleChoice = restrictions?.maxQuantity == 1
    return ModifierGroup(
        id = itemGroupId,
        name = name ?: "",
        items = items
            ?.map { it.toDomain() }
            ?.sortedBy { it.price != 0 }
            ?: emptyList(),
        isSingleChoice = isSingleChoice,
        isRequired = restrictions?.minQuantity?.let { it > 0 } == true,
        maxQuantity = restrictions?.maxQuantity ?: Int.MAX_VALUE
    )

}

fun BannerDto.toDomain() = Banner(
    imageUrl = imageUrl ?: "",
    targetName = targetName ?: "",
)

private fun getSafeModifiers(firstSize: ItemSizeDTO?) = firstSize
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

private data class BaseMealInfo(
    val weight: Int,
    val price: Int,
    val imageUrl: String?,
)

private fun extractBaseInfo(firstSize: ItemSizeDTO): BaseMealInfo {
    val weight = firstSize.portionWeightGrams.toInt()
    val price = firstSize.prices.firstOrNull()?.price?.toInt() ?: 0
    val imageUrl = firstSize.buttonImageUrl
    return BaseMealInfo(weight, price, imageUrl)
}

private fun isAddable(tags: List<Tag>): Boolean {
    val hasNoAdds = tags.any { it.name.equals(TAG_NO_ADDS, ignoreCase = true) }
    val hasAdds = tags.any { it.name.equals(TAG_ADDS, ignoreCase = true) }
    return !hasNoAdds && hasAdds
}

private fun isModifiable(modifiers: List<ModifierGroup>, price: Int): Boolean {
    val required = requireSelection(modifiers)
    return modifiers.isNotEmpty() && price > 0 && !required
}

private fun isDiscountable(tags: List<Tag>): Boolean {
    return tags.none { it.name.equals(TAG_NO_DISCOUNT, ignoreCase = true) }
}

private fun requireSelection(modifiers: List<ModifierGroup>): Boolean {
    return modifiers.any { it.isRequired }
}
