package com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.menu.domain.models.Meal
import com.mandarinkafe.mandarin.util.ui.components.UndoIndicator

@Composable
fun CartControlWithUndo(
    numberInCart: Int,
    totalPrice: Int,
    meal: Meal,
    mealInPendingDeletion: Boolean,
    deletionProgress: Float,
    onEvent: (CartContract.Event) -> Unit
) {

    val backgroundColor =
        if (mealInPendingDeletion) Colors.GreyTransparent10 else Colors.Orange.copy(alpha = 0.20f)

    Box(
        modifier = Modifier
            .height(Dimens.ButtonToCartSmall32)
            .widthIn(min = Dimens.ButtonToCartBig120)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(backgroundColor)
    )
    {
        if (!mealInPendingDeletion) {
            CartControls(
                modifier = Modifier.matchParentSize(),
                numberInCart = numberInCart,
                totalPrice = totalPrice,
                onEvent = onEvent,
                meal = meal,
            )
        } else {

            UndoIndicator(
                modifier = Modifier.matchParentSize(),
                progress = deletionProgress,
                onCancel = { onEvent(CartContract.Event.CancelRemove(meal)) },
            )

        }
    }
}
