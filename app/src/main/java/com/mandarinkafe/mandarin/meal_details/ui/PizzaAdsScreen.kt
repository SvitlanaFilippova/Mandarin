package com.mandarinkafe.mandarin.meal_details.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mandarinkafe.mandarin.menu.domain.models.Label
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.Tag

@Preview
@Composable
fun PizzaAdsScreenPreview() {
    PizzaAdsScreen(
        Meal(
            id = "1",
            name = "Маргарита",
            description = "Томатный соус, помидоры, моцарелла, орегано и базилик",
            weight = 490,
            price = 590,
            imageUrl = "https://optim.tildacdn.com/tild3064-3131-4362-b537-366634323165/-/resize/312x/-/format/webp/margaritta_veg.jpg",
            isFavorite = false,
            tags = listOf(
                Tag(
                    id = "1",
                    name = "добавки к пицце"
                )
            ),
            labels = listOf(
                Label(
                    code = "1",
                    name = "Veg"
                )
            ),
            isHidden = false,
            isEditable = true
        )
    )
}

@Composable
fun PizzaAdsScreen(meal: Meal) {

}