package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.pizza_ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.mappers.toMealAdditional
import com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.MealInfo
import com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.ToCartButton
import com.mandarinkafe.mandarin.menu.ui.view_model.meal_details.MealDetailsContract
import com.mandarinkafe.mandarin.menu.ui.view_model.meal_details.MealDetailsContract.Event

@Composable
fun PizzaAdsScreen(
    state: MealDetailsContract.State,
    onEvent: (Event) -> Unit,
) {
    val meal = state.meal ?: return // если meal null — не отображаем ничего
    val adds = state.pizzaAds
    val listState = rememberLazyListState()
    val selectedTabIndex = state.selectedTabIndex

    Column(
        modifier = Modifier
            .background(Colors.AppBlack)
            .padding(Dimens.MarginSmall8)
    )
    {
        MealInfo(
            meal = meal
        )

        Text(
            modifier = Modifier
                .padding(vertical = Dimens.MarginStandard16)
                .fillMaxWidth(),
            text = stringResource(R.string.adds),
            style = Typography.TitleStyle,
            textAlign = TextAlign.Center
        )

        AddsCategoryTabsRow(
            categories = adds.map { it.name },
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { index -> onEvent(Event.ChooseCategory(index)) }
        )
        AddsList(
            addsItems = adds[selectedTabIndex].meals?.map { it.toMealAdditional() },
            chosenAdds = meal.adds,
            listState = listState,
            modifier = Modifier.weight(1f),
            onCheckedChange = { isAdded, add ->
                onEvent(
                    Event.ChangeAdds(
                        add = add,
                        isAdded = isAdded
                    )
                )
            },

            )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.MarginStandard16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.price_with_adds),
                style = Typography.RegularTextStyle,
                textAlign = TextAlign.Start
            )

            Text(
                text = stringResource(R.string.meal_price_template, state.sumPrice),
                style = Typography.MealPriceStyle
            )
        }
        ToCartButton(
            onClick = { onEvent(Event.AddToCart) }
        )

    }
}
