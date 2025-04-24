package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.pizza_ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet.ToCartButton
import com.mandarinkafe.mandarin.menu.ui.view_model.meal_details.MealDetailsContract
import com.mandarinkafe.mandarin.menu.ui.view_model.meal_details.MealDetailsContract.Event

@Composable
fun PizzaAdsScreen(
    state: MealDetailsContract.State,
    onEvent: (Event) -> Unit,
    onCartEvent: (CartContract.Event) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val meal = state.meal ?: return
    val adds = state.pizzaAds
    val listState = rememberLazyListState()
    val selectedTabIndex = state.selectedTabIndex

    Column(
        modifier = modifier
            .background(Colors.AppBlack)
            .padding(Dimens.MarginSmall8)
    )
    {

        Text(
            modifier = Modifier
                .padding(vertical = Dimens.MarginSmall8)
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
            addsItems = adds[selectedTabIndex].mealAdditionals,
            modifier = Modifier.weight(1f),
            chosenAdds = meal.adds,
            listState = listState,
            onCheckedChange = { isChecked, add ->
                onEvent(
                    Event.ChangeAdds(
                        add = add,
                        isChecked = isChecked
                    )
                )
            }
        )

        ToCartButton(
            totalPrice = meal.price + meal.adds.sumOf { it.price },
            onClick = {
                onCartEvent(CartContract.Event.AddToCart(meal))
                onClose()
            }
        )

    }
}
