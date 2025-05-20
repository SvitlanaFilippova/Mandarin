package com.mandarinkafe.mandarin.util.ui.components.buttons

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.EditableType
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.CartMapper.toAddToCartEvent
import com.mandarinkafe.mandarin.features.cart.CartMapper.toRemoveFromCartNow
import com.mandarinkafe.mandarin.features.cart.getTotalPriceByMealId
import com.mandarinkafe.mandarin.features.cart.getTotalQuantityByMealId
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.menu.ui.components.mealitem.buttons.PizzaAddsButton
import com.mandarinkafe.mandarin.features.menu.ui.components.mealitem.buttons.SelectButton
import com.mandarinkafe.mandarin.features.menu.ui.components.mealitem.buttons.ToCartButtonWithPrice

@Composable
fun MealButtonsRow(
    meal: Meal,
    onMealDetailsClick: (Meal) -> Unit,
    onCartEvent: (CartContract.CartEvent) -> Unit,
    cartState: CartContract.CartState
) {
    val cartItems = cartState.cartItems
    val isInTheCart = cartItems.keys.any { it.meal.id == meal.id }
    val numberInCart = cartItems.getTotalQuantityByMealId(meal.id)
    val totalPrice = cartItems.getTotalPriceByMealId(meal.id)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = Dimens.MarginSmall8)
            .fillMaxWidth()
    ) {
        if (isInTheCart) {
            CartControls(
                totalPrice = totalPrice,
                numberInCart = numberInCart,
                onIncrease = { onCartEvent(meal.toAddToCartEvent()) },
                onDecrease = { onCartEvent(meal.toRemoveFromCartNow()) },
            )
        } else if (meal.editableType == EditableType.REQUIRED_SELECTION) {
            SelectButton(
                text = stringResource(R.string.to_choose),
                onClick = { onMealDetailsClick(meal) })
        } else {
            ToCartButtonWithPrice(
                price = meal.price, onClick = {
                    onCartEvent(meal.toAddToCartEvent())
                })
        }
        Spacer(Modifier.width(Dimens.MarginSmall8))

        if (meal.editableType == EditableType.PIZZA || meal.editableType == EditableType.ADDABLE) {
            PizzaAddsButton(
                onClick = { onMealDetailsClick(meal) }
            )
        }
    }
}
