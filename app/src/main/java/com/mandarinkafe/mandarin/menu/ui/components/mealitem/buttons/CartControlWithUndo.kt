package com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.cart.ui.view_model.CartContract
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.menu.domain.models.Meal

@Composable
fun CartControlWithUndo(
    numberInCart: Int,
    totalPrice: Int,
    meal: Meal,
    mealInPendingDeletion: Boolean,
    onEvent: (CartContract.Event) -> Unit
) {
    Box(
        modifier = Modifier
            .height(Dimens.ButtonToCartSmall32)
            .widthIn(min = Dimens.ButtonToCartBig120)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.GreyTransparent10)
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
            IconButton(
                onClick = { onEvent(CartContract.Event.CancelRemove(meal)) },
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.cancel_removing_full_text)
                )
                Text(
                    text = stringResource(R.string.cancel_removing),
                    style = Typography.ToCartButtonStyle
                )
            }
        }
    }
}