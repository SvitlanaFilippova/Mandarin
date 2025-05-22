package com.mandarinkafe.mandarin.util.ui.components.buttons

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.extensions.getTotalPriceByMealId
import com.mandarinkafe.mandarin.core.domain.models.extensions.getTotalQuantityByMealId
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomizable
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.CartMapper.toAddToCartEvent
import com.mandarinkafe.mandarin.features.cart.CartMapper.toRemoveFromCartNow
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract

@Composable
fun MealButtonsRow(
    meal: Meal,
    onMealDetailsClick: (Meal) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    cartState: CartContract.CartState,
    modifier: Modifier = Modifier
) {
    val cartItems = cartState.cartItems
    val isInTheCart = cartItems.keys.any { it.meal.id == meal.id }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val modifier = Modifier
            .widthIn(min = Dimens.ButtonToCartBig120)
            .height(Dimens.ButtonToCartSmall32)
            .weight(1f)

        if (meal.isCustomizable()) {
            CustomizeButton(
                modifier = Modifier.padding(end = Dimens.MarginSmall8),
                onClick = { onMealDetailsClick(meal) }
            )
        }

        if (isInTheCart) {
            val totalPrice = cartItems.getTotalPriceByMealId(meal.id)
            val numberInCart = cartItems.getTotalQuantityByMealId(meal.id)

            CartControls(
                totalPrice = totalPrice,
                numberInCart = numberInCart,
                onIncrease = { onCartEvent(meal.toAddToCartEvent()) },
                onDecrease = { onCartEvent(meal.toRemoveFromCartNow()) },
                modifier = modifier
            )
        } else if (meal.requireSelection) {
            SelectButton(
                text = stringResource(R.string.to_choose),
                onClick = { onMealDetailsClick(meal) },
                modifier = modifier
            )
        } else {
            ToCartButtonWithPrice(
                price = meal.price, onClick = {
                    onCartEvent(meal.toAddToCartEvent())
                },
                modifier = modifier
            )
        }
    }
}
