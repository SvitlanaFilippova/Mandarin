package com.mandarinkafe.mandarin.features.menu.ui.components.mealitem

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.util.ui.components.MealItemImageBox
import com.mandarinkafe.mandarin.util.ui.components.buttons.MealButtonsRow

@Composable
fun MenuCompactMealItem(
    meal: Meal,
    onEvent: (MenuEvent) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    cartState: CartContract.CartState,
    imageSize: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onEvent(MenuEvent.OnMealDetailsClick(meal)) }
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.GreyTransparent10)
            .padding(Dimens.MarginSmall8)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            MealItemImageBox(
                modifier = Modifier.size(imageSize),
                meal = meal,
                onToggleFavorite = { onEvent(MenuEvent.ToggleFavorite(meal)) },
            )

            Text(
                text = meal.name,
                style = Typography.MealTitleStyle,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.weight(1f))

            MealButtonsRow(
                meal = meal,
                onCartEvent = onCartEvent,
                cartState = cartState,
                onMealDetailsClick = { onEvent(MenuEvent.OnMealDetailsClick(meal)) }
            )
        }
    }
}
