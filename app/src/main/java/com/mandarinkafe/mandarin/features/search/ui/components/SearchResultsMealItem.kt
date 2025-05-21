package com.mandarinkafe.mandarin.features.search.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.search.ui.view_model.SearchContract.SearchEvent
import com.mandarinkafe.mandarin.util.ui.components.MealItemImageBox
import com.mandarinkafe.mandarin.util.ui.components.buttons.MealButtonsRow

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
            .size(Dimens.MealSmallImage80)
            .clickable { onSearchEvent(SearchEvent.OnMealDetailsClick(meal)) }
    ) {
        MealItemImageBox(
            modifier = Modifier.size(Dimens.MealSmallImage80),
            meal = meal,
            onToggleFavorite = { onSearchEvent(SearchEvent.ToggleFavorite(meal)) },
        )

        Column(
            modifier = Modifier
                .padding(horizontal = Dimens.MarginSmall8)
                .fillMaxSize()
        )
        {
            Text(
                text = meal.name,
                style = Typography.MealTitleStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )

            Text(
                text = meal.description,
                style = Typography.MealSmallTextStyle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Spacer(modifier = Modifier.weight(1f))

            MealButtonsRow(
                meal = meal,
                onCartEvent = onCartEvent,
                cartState = cartState,
                onMealDetailsClick = { meal -> onSearchEvent(SearchEvent.OnMealDetailsClick(meal)) },
            )
        }
    }
}