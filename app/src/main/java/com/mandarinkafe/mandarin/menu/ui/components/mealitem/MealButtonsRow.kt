package com.mandarinkafe.mandarin.menu.ui.components.mealitem

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.cart.ui.view_model.isInCarById
import com.mandarinkafe.mandarin.cart.ui.view_model.quantityById
import com.mandarinkafe.mandarin.core.domain.models.EditableType
import com.mandarinkafe.mandarin.core.domain.models.Meal
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons.CartControlWithUndo
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons.FavoriteButton
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons.PizzaAddsButton
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons.ToCartButtonWithPrice
import com.mandarinkafe.mandarin.menu.ui.view_model.MenuContract.Event

@Composable
fun MealButtonsRow(
    meal: Meal,
    onEvent: (Event) -> Unit,
    onCartEvent: (CartContract.Event) -> Unit,
    cartState: CartContract.State
) {

    val isInTheCart = cartState.cartItems.isInCarById(meal.id)
    val numberInCart = cartState.cartItems.quantityById(meal.id)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = Dimens.MarginSmall8)
            .fillMaxWidth()
    ) {
        if (isInTheCart) {
            CartControlWithUndo(
                numberInCart = numberInCart,
                totalPrice = meal.price,
                meal = meal,
                onEvent = onCartEvent,
                mealInPendingDeletion = cartState.pendingDeletionMeals.contains(meal),
                deletionProgress = cartState.mealDeletionProgress[meal] ?: 0f,
            )
        } else {
            ToCartButtonWithPrice(meal.price, onClick = {
                onCartEvent(CartContract.Event.AddToCart(meal))
            })
        }

        if (meal.editableType == EditableType.PIZZA) {
            PizzaAddsButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    onEvent(Event.OnMealCustomizationClick(meal))
                }
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        FavoriteButton(
            isFavorite = meal.isFavorite,
            onClick = { onEvent(Event.ToggleFavorite(meal)) }
        )
    }
}

