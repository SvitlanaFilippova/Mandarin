package com.mandarinkafe.mandarin.features.menu.ui.components.mealitem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_MEAL_DESCRIPTION_IN_MENU
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_MEAL_TITLE_IN_MENU
import com.mandarinkafe.mandarin.util.ui.components.MealItemImageBox
import com.mandarinkafe.mandarin.util.ui.components.buttons.MealButtonsRow

@Composable
fun MenuMealItem(
    meal: Meal,
    onEvent: (MenuEvent) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    cartState: CartContract.CartState,
    imageSize: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .padding(horizontal = Dimens.MarginSmall8)
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.GreyTransparent10)
            .clickable(onClick = { onEvent(MenuEvent.OnMealDetailsClick(meal)) })
    ) {
        MealItemImageBox(
            modifier = Modifier
                .size(imageSize)
                .padding(Dimens.MarginSmall8),
            meal = meal,
            onToggleFavorite = { meal -> onEvent(MenuEvent.ToggleFavorite(meal)) },
        )

        // Блок с текстовой информацией о блюде
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.MarginSmall8)
        ) {
            Text(
                text = meal.name,
                style = Typography.MealTitleStyle,
                maxLines = MAX_LINES_FOR_MEAL_TITLE_IN_MENU,
                overflow = TextOverflow.Ellipsis
            )

            if (meal.description.isNotEmpty()) {
                Text(
                    text = meal.description,
                    style = Typography.MealSmallTextStyle,
                    maxLines = MAX_LINES_FOR_MEAL_DESCRIPTION_IN_MENU,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (meal.weight != 0) {
                Text(
                    modifier = Modifier.padding(vertical = Dimens.MarginSuperSmall4),
                    text = stringResource(R.string.meal_weight_template, meal.weight),
                    style = Typography.MealSmallTextStyle
                )
            }
            Spacer(
                modifier = Modifier.weight(1f)
            )
            // Контейнер для кнопок
            Box(contentAlignment = Alignment.BottomStart) {
                MealButtonsRow(
                    meal = meal,
                    onCartEvent = onCartEvent,
                    cartState = cartState,
                    onMealDetailsClick = { meal -> onEvent(MenuEvent.OnMealDetailsClick(meal)) },
                )
            }
        }
    }
}
