package com.mandarinkafe.mandarin.menu.data.mapper

import com.mandarinkafe.mandarin.core.domain.models.EditableType
import com.mandarinkafe.mandarin.core.domain.models.Label
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.MealCategory
import com.mandarinkafe.mandarin.core.domain.models.ModifierGroup
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.domain.models.Tag
import com.mandarinkafe.mandarin.menu.data.dto.CategoryDto
import com.mandarinkafe.mandarin.menu.data.dto.LabelDto
import com.mandarinkafe.mandarin.menu.data.dto.MealDto
import com.mandarinkafe.mandarin.menu.data.dto.ModifierGroupDto
import com.mandarinkafe.mandarin.menu.data.dto.ModifierItemDto
import com.mandarinkafe.mandarin.menu.data.dto.TagDto
import com.mandarinkafe.mandarin.util.Constants.TAG_PIZZA_ADDS
import com.mandarinkafe.mandarin.util.Constants.TAG_WOK_CONSTRUCTOR
import com.mandarinkafe.mandarin.util.applyTypography

fun CategoryDto.toDomain(storedFavorites: List<String>): MealCategory {
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
        val safePrice = prices?.firstOrNull()?.price?.toInt() ?: 0

        return ModifierItem(
            id = itemId,
            name = name ?: "",
            price = safePrice,
        )
    }

    fun ModifierGroupDto.toDomain() = ModifierGroup(
        id = itemGroupId,
        name = name ?: "",
        items = items?.map { it.toDomain() } ?: emptyList(),
        isSingleChoice = (restrictions?.maxQuantity == 1),
    )

