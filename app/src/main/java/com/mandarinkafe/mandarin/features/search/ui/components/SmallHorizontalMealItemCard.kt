package com.mandarinkafe.mandarin.features.search.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.SearchEvent
import com.mandarinkafe.mandarin.util.ui.components.MealItemImageBox
import com.mandarinkafe.mandarin.util.ui.components.buttons.MealButtonsRow

@Composable
fun SmallHorizontalMealItemCard(
    meal: Meal,
    onSearchEvent: (SearchEvent) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    cartState: CartContract.CartState,
    modifier: Modifier = Modifier
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.MarginSmall8)
            .clickable { onSearchEvent(SearchEvent.OnMealDetailsClick(meal)) }
    ) {
        MealItemImageBox(
            modifier = Modifier.size(Dimens.MealSmallImage80),
            meal = meal,
            onToggleFavorite = { onSearchEvent(SearchEvent.ToggleFavorite(meal)) },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Dimens.MarginSmall8)
        ) {
            // Название блюда
            Text(
                text = meal.name,
                style = Typography.RegularTextStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
            )

            // Описание в 1 строку
            Text(
                text = meal.description,
                style = Typography.MealSmallTextStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                if (meal.weight != 0) {
                    Text(
                        text = stringResource(R.string.meal_weight_template, meal.weight),
                        style = Typography.MealSmallTextStyle
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                MealButtonsRow(
                    meal = meal,
                    onCartEvent = onCartEvent,
                    cartState = cartState,
                    onMealDetailsClick = { meal -> onSearchEvent(SearchEvent.OnMealDetailsClick(meal)) },
                    modifier = Modifier
                        .width(Dimens.ButtonsRowWidth164)
                        .padding(top = Dimens.MarginSmall8)
                )
            }
        }
    }
}

