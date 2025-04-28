package com.mandarinkafe.mandarin.cart.ui.components

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.cart.ui.view_model.totalPrice
import com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons.CartControls
import com.mandarinkafe.mandarin.util.ui.components.UndoIndicator

@Composable
fun CartControlWithUndo(
    numberInCart: Int,
    item: CartItem,
    mealInPendingDeletion: Boolean,
    deletionProgress: Float,
    onEvent: (CartContract.Event) -> Unit
) {

    if (!mealInPendingDeletion) {
        CartControls(
            numberInCart = numberInCart,
            totalPrice = item.totalPrice(),
            onIncrease = { onEvent(CartContract.Event.AddToCart(item)) },
            onDecrease = { onEvent(CartContract.Event.RemoveFromCartWithDelay(item)) },
        )
    } else {
        UndoIndicator(
            progress = deletionProgress,
            onCancel = { onEvent(CartContract.Event.CancelRemove(item)) },
        )

    }
}

