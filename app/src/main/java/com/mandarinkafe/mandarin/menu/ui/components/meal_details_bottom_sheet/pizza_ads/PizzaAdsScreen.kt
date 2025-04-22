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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.MealInfo
import com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.ToCartButton
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event

@Composable
fun PizzaAdsScreen(
    meal: Meal,
    onEvent: (Event) -> Unit,
    adds: List<MealCategory>,
    onClose: () -> Unit,
) {
    val listState = rememberLazyListState()
    var sumPrice by remember {
        mutableIntStateOf(meal.price)
    }
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

    Column(
        modifier = Modifier
            .background(Colors.AppBlack)
            .padding(Dimens.MarginSmall8)
    )
    {
        MealInfo(
            meal = meal,
            onToggleFavorite = { onEvent(Event.ToggleFavorite(meal)) },
            onClose = onClose
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
            onTabSelected = { tab -> selectedTabIndex = tab }
        )
        AddsList(
            addsItems = adds[selectedTabIndex].meals,
            listState = listState,
            modifier = Modifier.weight(1f),
            onEvent = { }
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
                text = stringResource(R.string.meal_price_template, sumPrice),
                style = Typography.MealPriceStyle
            )
        }
        ToCartButton(
            onClick = {}
        )

    }
}
