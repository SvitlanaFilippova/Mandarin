package com.mandarinkafe.mandarin.util.presentation.ui.components.buttons

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.domain.models.isCustomizable
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.data.getTotalPriceByMealId
import com.mandarinkafe.mandarin.features.cart.data.getTotalQuantityByMealId
import com.mandarinkafe.mandarin.util.presentation.ui.components.ButtonWithCircularProgressIndicator

@Composable
fun MealButtonsRow(
    modifier: Modifier = Modifier,
    baseMeal: Meal,
    isInProgress: Boolean,
    cartItems: List<CartItem>,
    onMealDetailsClick: () -> Unit,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit,
) {
    val isInTheCart = cartItems.any { it.customizedMeal.meal.id == baseMeal.id }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val modifier = Modifier
            .widthIn(min = Dimens.ButtonToCartBig120)
            .height(Dimens.ButtonToCartSmall36)
            .weight(1f)

        if (baseMeal.isCustomizable) {
            CustomizeButton(
                modifier = Modifier.padding(end = Dimens.MarginSmall8),
                onClick = onMealDetailsClick
            )
        }

        when {
            isInProgress -> ButtonWithCircularProgressIndicator(modifier = modifier)

            isInTheCart -> {
                val totalPrice = cartItems.getTotalPriceByMealId(baseMeal.id)
                val numberInCart = cartItems.getTotalQuantityByMealId(baseMeal.id)
                CartControls(
                    modifier = modifier,
                    numberInCart = numberInCart,
                    totalPrice = totalPrice,
                    onIncrease = onAddToCart,
                    onDecrease = onRemoveFromCart,
                )
            }

            baseMeal.requireSelection -> {
                SelectButton(
                    text = stringResource(R.string.to_choose),
                    onClick = onMealDetailsClick,
                    modifier = modifier
                )
            }

            else -> {
                ToCartButtonWithPrice(
                    price = baseMeal.price,
                    onClick = onAddToCart,
                    modifier = modifier,
                )
            }
        }
    }
}
