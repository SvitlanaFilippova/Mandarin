package com.mandarinkafe.mandarin.menu.ui.components.mealitem

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.CartMapper.toAddToCartEvent
import com.mandarinkafe.mandarin.cart.CartMapper.toRemoveFromCartNow
import com.mandarinkafe.mandarin.cart.getTotalPriceByMealId
import com.mandarinkafe.mandarin.cart.getTotalQuantityByMealId
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.core.domain.models.EditableType
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons.CartControls
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons.FavoriteButton
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons.PizzaAddsButton
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons.SelectButton
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons.ToCartButtonWithPrice
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event

@Composable
fun MealButtonsRow(
    meal: Meal,
    onMenuEvent: (Event) -> Unit,
    onCartEvent: (CartContract.Event) -> Unit,
    cartState: CartContract.State
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
        } else if (meal.editableType == EditableType.MODIFIABLE) {
            SelectButton(
                text = stringResource(R.string.to_choose), onClick = {
                    onMenuEvent(Event.OnMealDetailsClick(meal))
                })
        } else if (meal.editableType == EditableType.WOK) {
            SelectButton(
                text = stringResource(R.string.create_own_box_short), onClick = {
                    onMenuEvent(Event.OnMealDetailsClick(meal))
                })
        } else {
            ToCartButtonWithPrice(
                price = meal.price, onClick = {
                    onCartEvent(meal.toAddToCartEvent())
                })
        }

        if (meal.editableType == EditableType.PIZZA) {
            PizzaAddsButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    onMenuEvent(Event.OnMealDetailsClick(meal))
                }
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        FavoriteButton(
            isFavorite = meal.isFavorite,
            onClick = { onMenuEvent(Event.ToggleFavorite(meal)) }
        )
    }
}

