package com.mandarinkafe.mandarin.features.menu.presentation.ui.components.mealitem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.extensions.isFavorite
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.util.presentation.ui.components.MealItemImageBox
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.MealButtonsRow

@Composable
fun MenuCompactMealItem(
    modifier: Modifier = Modifier,
    meal: Meal,
    favoriteIds: Set<String>,
    cartState: CartContract.CartState,
    imageSize: Dp,
    onToggleFavorite: (Meal) -> Unit,
    onAddToCart: (Meal) -> Unit,
    onRemoveFromCart: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
) {

    val isFavorite by remember(favoriteIds) { derivedStateOf { meal.isFavorite(favoriteIds) } }

    Box(
        modifier = modifier
            .clickable { onMealDetailsClick(meal) }
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.DarkGrey)
            .padding(Dimens.MarginSmall8)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            MealItemImageBox(
                modifier = Modifier.size(imageSize),
                meal = meal,
                onToggleFavorite = { onToggleFavorite(meal) },
                isFavorite = isFavorite,
            )

            Text(
                modifier = Modifier.padding(vertical = Dimens.MarginSmall8),
                text = meal.name,
                style = Typography.MealTitleStyle,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            MealButtonsRow(
                baseMeal = meal,
                onAddToCart = { onAddToCart(meal) },
                onRemoveFromCart = { onRemoveFromCart(meal) },
                cartState = cartState,
                onMealDetailsClick = { onMealDetailsClick(meal) }
            )
        }
    }
}
