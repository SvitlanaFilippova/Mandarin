package com.mandarinkafe.mandarin.features.favorites.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.CustomizedMeal
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomizable
import com.mandarinkafe.mandarin.core.domain.models.extensions.isCustomized
import com.mandarinkafe.mandarin.core.domain.models.extensions.totalPrice
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.shared.cart.ui.view_model.CartContract.CartState
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.CartControls
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.CustomizeButton
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.SelectButton
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ToCartButtonWithPrice

@Composable
fun FavoriteItemButtonRow(
    modifier: Modifier = Modifier,
    item: CustomizedMeal,
    onAddToCart: (CustomizedMeal) -> Unit,
    onRemoveFromCart: (CustomizedMeal) -> Unit,
    onMealDetailsClick: (CustomizedMeal) -> Unit,
    cartState: CartState,
) {
    val cartItems = cartState.cartItems
    val isInTheCart = cartItems.keys.any { it == item }
    val totalPrice = item.totalPrice()
    val isCustomized = item.isCustomized()


    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        val modifier = Modifier
            .widthIn(min = Dimens.ButtonToCartBig120)
            .height(Dimens.ButtonToCartSmall32)
            .weight(1f)

        if (item.meal.isCustomizable()) {
            CustomizeButton(
                modifier = Modifier.padding(end = Dimens.MarginSmall8),
                onClick = { onMealDetailsClick(item) }
            )
        }

        if (isInTheCart) {
            val numberInCart = cartItems[item] ?: 0
            CartControls(
                totalPrice = totalPrice * numberInCart,
                numberInCart = numberInCart,
                onIncrease = { onAddToCart(item) },
                onDecrease = { onRemoveFromCart(item) },
                modifier = modifier
            )

        } else if (item.meal.requireSelection && !isCustomized) {
            SelectButton(
                text = stringResource(R.string.to_choose),
                onClick = { onMealDetailsClick(item) },
                modifier = modifier
            )
        } else {
            ToCartButtonWithPrice(
                price = totalPrice, onClick = {
                    onAddToCart(item)
                },
                modifier = modifier
            )
        }
    }
}
