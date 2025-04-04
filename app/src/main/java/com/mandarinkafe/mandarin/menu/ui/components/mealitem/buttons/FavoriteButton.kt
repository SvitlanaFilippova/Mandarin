package com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Meal

@Composable
fun FavoriteButton(
    meal: Meal,
    onToggleFavorite: (Meal) -> Unit = {},
) {
    IconButton(
        onClick = { onToggleFavorite(meal) },
        modifier = Modifier.size(Dimens.ButtonToCartSmall32)
    ) {
        Icon(
            painter = painterResource(
                if (meal.isFavorite) R.drawable.ic_favorite_active
                else R.drawable.ic_favorite_inactive
            ),
            contentDescription = "Добавить в избранное",
            modifier = Modifier.size(Dimens.ButtonToggleFavorite28),
            tint = Color.Unspecified
        )
    }
}