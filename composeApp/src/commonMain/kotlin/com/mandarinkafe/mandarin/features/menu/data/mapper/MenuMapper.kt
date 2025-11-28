package com.mandarinkafe.mandarin.features.menu.data.mapper

import com.mandarinkafe.mandarin.core.domain.models.Label
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MeasureUnitType
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.domain.models.Tag
import com.mandarinkafe.mandarin.features.menu.data.dto.ItemSizeDto
import com.mandarinkafe.mandarin.features.menu.data.dto.LabelDto
import com.mandarinkafe.mandarin.features.menu.data.dto.MealDto
import com.mandarinkafe.mandarin.features.menu.data.dto.ModifierGroupDto
import com.mandarinkafe.mandarin.features.menu.data.dto.ModifierItemDto
import com.mandarinkafe.mandarin.features.menu.data.dto.TagDto
import com.mandarinkafe.mandarin.features.menu.domain.models.MealPickupPoint
import com.mandarinkafe.mandarin.BuildKonfig
import com.mandarinkafe.mandarin.util.Constants.TAG_CAFE
import com.mandarinkafe.mandarin.util.Constants.TAG_IS_DELIVERY_POSITION
import com.mandarinkafe.mandarin.util.Constants.TAG_NO_ADDS
import com.mandarinkafe.mandarin.util.Constants.TAG_NO_DELIVERY
import com.mandarinkafe.mandarin.util.Constants.TAG_NO_DISCOUNT
import com.mandarinkafe.mandarin.util.Constants.TAG_PIZZERIA
import com.mandarinkafe.mandarin.util.applyTypography
import com.mandarinkafe.mandarin.util.removeLeadingDash

fun MealDto.toDomain(
    categoryLabels: List<Label>,
    categoryTags: List<Tag>,
    categoryPath: List<String>,
    isAddable: Boolean,
): Meal? {
    val firstSize = itemSizes?.firstOrNull() ?: return null
    val baseInfo = extractBaseInfo(firstSize) ?: return null
    val safeModifiers = getSafeModifiers(firstSize)

    val mealLabels = (labels ?: emptyList()).map { it.toDomain() }
    val mealTags = (tags ?: emptyList()).map { it.toDomain() }

    val finalMealLabels = mergeLabels(mealLabels, categoryLabels)
    val finalMealTags = mergeTags(mealTags, categoryTags)

    val isPickupOnly = finalMealTags.any { it.name.equals(TAG_NO_DELIVERY, ignoreCase = true) }
    val mealName = name.applyTypography()

    return Meal(
        id = itemId,
        name = mealName,
        description = (description ?: "").applyTypography(),
        sku = sku ?: "",
        weight = baseInfo.weight,
        measureUnitType = MeasureUnitType.from(firstSize.measureUnitType) ?: MeasureUnitType.GRAM,
        price = baseInfo.price,
        mainImageUrl = baseInfo.imageUrl ?: "",
        smallImageUrl = baseInfo.thumbnailUrl ?: "",
        blurredPreviewUrl = baseInfo.placeholderUrl ?: "",
        labels = finalMealLabels,
        tags = finalMealTags,
        isHidden = isHidden == true,
        modifiers = safeModifiers,
        isAddable = checkIfAddable(tags = finalMealTags, catIsAddable = isAddable),
        requireSelection = requireSelection(safeModifiers),
        isModifiable = isModifiable(safeModifiers, baseInfo.price),
        isPickupOnly = isPickupOnly,
        discountable = isDiscountable(finalMealTags),
        pickupPoint = resolvePickupPoint(finalMealTags),
        orderItemType = orderItemType,
        categoryPath = categoryPath,
        isDelivery = finalMealTags.any {
            it.name.equals(
                TAG_IS_DELIVERY_POSITION,
                ignoreCase = true
            )
        },
    )
}

fun TagDto.toDomain() = Tag(
    id = id,
    name = name
)

fun LabelDto.toDomain() = Label(
    id = code,
    name = name.takeIf { it.uppercase() == it } // если всё заглавные, оставляем как есть
        ?: name.replaceFirstChar { it.uppercaseChar() } // иначе делаем первую букву заглавной
)

fun ModifierItemDto.toDomain(): ModifierItem {
    val safePrice = prices?.firstOrNull()?.price?.toInt() ?: 0
    val measureUnitType = MeasureUnitType.from(measureUnitType) ?: MeasureUnitType.GRAM
    val weight = (portionWeightGrams ?: 0.0).toFloat()

    return ModifierItem(
        id = itemId,
        name = name?.removeLeadingDash()?.applyTypography() ?: "",
        price = safePrice,
        weight = weight,
        measureUnitType = measureUnitType,
    )
}

fun ModifierGroupDto.toDomain(): ModifierGroup {
    val isSingleChoice = restrictions?.maxQuantity == 1
    return ModifierGroup(
        id = itemGroupId,
        name = name?.applyTypography() ?: "",
        items = items
            ?.map { it.toDomain() }
            ?.sortedBy { it.price != 0 }
            ?: emptyList(),
        isSingleChoice = isSingleChoice,
        isRequired = restrictions?.minQuantity?.let { it > 0 } == true,
        maxQuantity = restrictions?.maxQuantity ?: 0
    )

}

private fun getSafeModifiers(firstSize: ItemSizeDto?) = firstSize
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
    val weight: Float,
    val price: Int,
    val imageUrl: String?,
    val thumbnailUrl: String?,
    val placeholderUrl: String?,
)

private fun extractBaseInfo(firstSize: ItemSizeDto): BaseMealInfo? {
    val weight = firstSize.portionWeightGrams ?: 0f
    val price = firstSize.prices?.firstOrNull()?.price?.toInt() ?: return null
    val imageUrl = firstSize.buttonImageUrl
    val thumbnailUrl = BuildKonfig.SERVER_BASE_URL + firstSize.thumbnailUrl
    val blurredPreviewUrl = BuildKonfig.SERVER_BASE_URL + firstSize.placeholderUrl
    return BaseMealInfo(weight, price, imageUrl, thumbnailUrl, blurredPreviewUrl)
}

private fun checkIfAddable(tags: List<Tag>, catIsAddable: Boolean): Boolean {
    val hasNoAdds = tags.any { it.name.equals(TAG_NO_ADDS, ignoreCase = true) }
    return !hasNoAdds && catIsAddable
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

private fun resolvePickupPoint(tags: List<Tag>): MealPickupPoint {
    return when {
        tags.any { it.name.equals(TAG_PIZZERIA, ignoreCase = true) } -> MealPickupPoint.PIZZERIA
        tags.any { it.name.equals(TAG_CAFE, ignoreCase = true) } -> MealPickupPoint.CAFE
        else -> MealPickupPoint.ANY
    }
}

