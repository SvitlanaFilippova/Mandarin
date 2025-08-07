package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.totalPrice
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.UndoIndicator
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.CartControls

@Composable
fun CartControlWithUndo(
    item: CartItem,
    mealInPendingDeletion: Boolean,
    deletionProgress: Float,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit,
    onCancel: () -> Unit,
) {
    if (!mealInPendingDeletion) {
        CartControls(
            numberInCart = item.quantity,
            totalPrice = item.customizedMeal.totalPrice() * item.quantity,
            onIncrease = onAddToCart,
            onDecrease = onRemoveFromCart,
            modifier = Modifier
                .widthIn(min = Dimens.ButtonToCartBig120)
                .height(Dimens.ButtonToCartSmall32)
        )
    } else {
        UndoIndicator(
            progress = deletionProgress,
            onCancel = onCancel,
        )

    }
}

