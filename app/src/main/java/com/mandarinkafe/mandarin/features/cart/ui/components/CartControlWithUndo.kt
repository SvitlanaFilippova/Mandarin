package com.mandarinkafe.mandarin.features.cart.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.features.cart.domain.model.CartItem
import com.mandarinkafe.mandarin.features.cart.domain.model.extensions.totalPrice
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.util.ui.components.UndoIndicator
import com.mandarinkafe.mandarin.util.ui.components.buttons.CartControls

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
            modifier = Modifier
                .widthIn(min = Dimens.ButtonToCartBig120)
                .height(Dimens.ButtonToCartSmall32)
        )
    } else {
        UndoIndicator(
            progress = deletionProgress,
            onCancel = { onEvent(CartContract.CartEvent.CancelRemove(item)) },
        )

    }
}

