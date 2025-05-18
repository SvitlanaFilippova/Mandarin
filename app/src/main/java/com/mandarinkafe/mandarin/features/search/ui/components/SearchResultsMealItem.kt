package com.mandarinkafe.mandarin.features.search.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.mandarinkafe.mandarin.features.menu.ui.components.mealitem.MealButtonsRow
import com.mandarinkafe.mandarin.features.search.SearchMapper.toUiModel
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.SearchEvent
import com.mandarinkafe.mandarin.util.ui.components.LabelChip

@Composable
fun SearchResultsMealItem(
    meal: Meal,
    onSearchEvent: (SearchEvent) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    cartState: CartContract.CartState,
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = Dimens.MarginSmall8)
            .fillMaxWidth()
            .clickable { onSearchEvent(SearchEvent.OnMealDetailsClick(meal)) }
    ) {

        Box(
            modifier = Modifier
                .size(Dimens.MealSmallImage80)
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
                .padding(start = Dimens.MarginSmall8),
        )
        {
            Text(
                text = meal.name,
                style = Typography.MealTitleStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )

            MealButtonsRow(
                meal = meal,
                onCartEvent = onCartEvent,
                cartState = cartState,
                onToggleFavorite = { meal -> onSearchEvent(SearchEvent.ToggleFavorite(meal)) },
                onMealDetailsClick = { meal -> onSearchEvent(SearchEvent.OnMealDetailsClick(meal)) },
            )
        }
    }
}