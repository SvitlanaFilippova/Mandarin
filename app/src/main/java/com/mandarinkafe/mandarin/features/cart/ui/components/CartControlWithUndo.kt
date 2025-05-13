package com.mandarinkafe.mandarin.features.cart.ui.components

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.features.cart.totalPrice
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.features.menu.ui.components.mealitem.buttons.CartControls
import com.mandarinkafe.mandarin.util.ui.components.UndoIndicator

@Composable
fun CartControlWithUndo(
    numberInCart: Int,
    item: CartItem,
    mealInPendingDeletion: Boolean,
    deletionProgress: Float,
    onEvent: (CartContract.CartEvent) -> Unit
) {

    if (!mealInPendingDeletion) {
        CartControls(
            numberInCart = numberInCart,
            totalPrice = item.totalPrice() * numberInCart,
            onIncrease = { onEvent(CartContract.CartEvent.AddToCart(item)) },
            onDecrease = { onEvent(CartContract.CartEvent.RemoveFromCartWithDelay(item)) },
        )
    } else {
        UndoIndicator(
            progress = deletionProgress,
            onCancel = { onEvent(CartContract.CartEvent.CancelRemove(item)) },
        )

    }
}

