package com.mandarinkafe.mandarin.features.menu.presentation.ui.components.mealitem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.isFavorite
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_MEAL_DESCRIPTION_IN_MENU
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_MEAL_TITLE_IN_MENU
import com.mandarinkafe.mandarin.util.LabelSize
import com.mandarinkafe.mandarin.util.presentation.localizedShortText
import com.mandarinkafe.mandarin.util.presentation.ui.components.images.MealItemImageBox
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.MealButtonsRow
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MenuMealItem(
    modifier: Modifier = Modifier,
    meal: Meal,
    onToggleFavorite: (Meal) -> Unit,
    onAddToCart: (Meal) -> Unit,
    favoriteIds: Set<String>,
    onRemoveFromCart: (Meal) -> Unit,
    onMealDetailsClick: (Meal) -> Unit,
    cartItems: List<CartItem>,
    isInProgress: Boolean,
    imageSize: Dp,
) {
    val isFavorite by remember(favoriteIds) {
        derivedStateOf { meal.isFavorite(favoriteIds) }
    }

    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .padding(horizontal = Dimens.MarginSmall8)
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.DarkGrey)
            .clickable(onClick = { onMealDetailsClick(meal) })
    ) {
        MealItemImageBox(
            modifier = Modifier
                .size(imageSize)
                .padding(Dimens.MarginSmall8),
            meal = meal,
            cardIsSmall = false,
            isFavorite = isFavorite,
            onToggleFavorite = { onToggleFavorite(meal) },
            labelSize = LabelSize.MEDIUM,
        )

        // Блок с текстовой информацией о блюде
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = Dimens.MarginSmall8,
                    end = Dimens.MarginSmall8,
                    bottom = Dimens.MarginSmall8
                )
        ) {
            Text(
                text = meal.name,
                style = Typography.MealTitleStyle,
                maxLines = MAX_LINES_FOR_MEAL_TITLE_IN_MENU,
                overflow = TextOverflow.Ellipsis
            )

            if (meal.description.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(vertical = Dimens.MarginSuperSmall4),
                    text = meal.description,
                    style = Typography.MealSmallTextStyle,
                    maxLines = MAX_LINES_FOR_MEAL_DESCRIPTION_IN_MENU,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (meal.weight != 0) {
                Text(
                    modifier = Modifier.padding(vertical = Dimens.MarginSmall8),
                    text = stringResource(
                        MR.strings.meal_weight_template,
                        meal.weight,
                        meal.measureUnitType.localizedShortText()
                    ),
                    style = Typography.MealSmallTextStyle
                )
            }
            // Для выравнивания кнопок
            Spacer(
                modifier = Modifier.weight(1f)
            )
            // Кнопки
            MealButtonsRow(
                baseMeal = meal,
                onAddToCart = { onAddToCart(meal) },
                onRemoveFromCart = { onRemoveFromCart(meal) },
                cartItems = cartItems,
                onMealDetailsClick = { onMealDetailsClick(meal) },
                isInProgress = isInProgress,
            )

        }
    }
}
