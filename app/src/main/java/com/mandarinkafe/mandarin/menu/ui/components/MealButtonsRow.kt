package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.ui.components.buttons.CartControls
import com.mandarinkafe.mandarin.menu.ui.components.buttons.EditMealButton
import com.mandarinkafe.mandarin.menu.ui.components.buttons.FavoriteButton
import com.mandarinkafe.mandarin.menu.ui.components.buttons.ToCartButton

@Composable
fun MealButtonsRow(meal: Meal) {
    var isInTheCart by remember { mutableStateOf(false) }
    var numberInCart by remember { mutableIntStateOf(1) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = Dimens.MarginSmall8)
            .fillMaxWidth()
    ) {
        if (isInTheCart) {
            CartControls(
                numberInCart = numberInCart,
                price = meal.price,
                onIncrease = { numberInCart++ },
                onDecrease = {
                    if (numberInCart > 1) numberInCart-- else isInTheCart = false
                }
            )
        } else {
            ToCartButton(meal.price) {
                isInTheCart = true
                /* Действие */
            }
        }

        if (meal.isEditable) {
            EditMealButton(modifier = Modifier.weight(1f))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        FavoriteButton(isFavorite = meal.isFavorite)
    }
}
