package com.mandarinkafe.mandarin.features.favorites.data.mapper

import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.features.favorites.domain.models.FavoriteMeal
import com.mandarinkafe.mandarin.features.menu.domain.models.MenuItem.MealItem

object FavoriteMapper {
    fun Meal.toFavoriteMeal() = FavoriteMeal(
        id = id,
        name = name,
        description = description,
        weight = weight,
        price = price,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        tags = tags,
        labels = labels,
        isHidden = isHidden,
        editableType = editableType,
        modifiers = modifiers
    )

    fun FavoriteMeal.toMealItem() = MealItem(
        Meal(
            id = id,
            name = name,
            description = description,
            weight = weight,
            price = price,
            imageUrl = imageUrl,
            isFavorite = isFavorite,
            tags = tags,
            labels = labels,
            isHidden = isHidden,
            editableType = editableType,
            modifiers = modifiers
        )
    )
}