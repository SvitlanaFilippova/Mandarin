package com.mandarinkafe.mandarin.features.menu.ui.components.mealitem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.menu.ui.view_model.MenuContract.MenuEvent
import com.mandarinkafe.mandarin.features.search.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.util.ui.components.LabelChip

@Composable
fun MenuMealItem(
    meal: Meal,
    onEvent: (MenuEvent) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    cartState: CartContract.CartState
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .padding(Dimens.MarginSmall8)
            .clickable(onClick = { onEvent(MenuEvent.OnMealDetailsClick(meal)) })
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.MealImage136)
        ) {
            AsyncImage(
                model = meal.imageUrl.ifEmpty { R.drawable.placeholder_meal_no_photo },
                contentDescription = stringResource(R.string.picture_of_meal_template, meal.name),
                error = painterResource(R.drawable.placeholder_meal_no_photo),
                placeholder = painterResource(R.drawable.placeholder_meal_no_photo),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(Dimens.CornerRadius8))
                    .background(
                        color = Colors.AppBlack,
                        shape = RoundedCornerShape(Dimens.CornerRadius8)
                    )
            )

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Dimens.MarginSuperSmall4)
            ) {
                meal.labels.forEach {
                    LabelChip(
                        label = it.toUiModel(),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(start = Dimens.MarginSmall8)
        )
        {

            // Блок с текстовой информацией о блюде
            Column(modifier = Modifier.heightIn(min = Dimens.MealMinDescriptionHeight96)) {
                Text(
                    text = meal.name,
                    style = Typography.MealTitleStyle,
                    maxLines = 3
                )

                if (meal.description.isNotEmpty()) {
                    Text(
                        text = meal.description,
                        style = Typography.MealSmallTextStyle,
                        maxLines = 4,
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
            }
            // Контейнер для кнопок
            Box(contentAlignment = Alignment.BottomStart) {
                MealButtonsRow(
                    meal = meal,
                    onCartEvent = onCartEvent,
                    cartState = cartState,
                    onToggleFavorite = { meal -> onEvent(MenuEvent.ToggleFavorite(meal)) },
                    onMealDetailsClick = { meal -> onEvent(MenuEvent.OnMealDetailsClick(meal)) },
                )
            }
        }
    }
}
