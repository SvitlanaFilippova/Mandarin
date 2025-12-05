package com.mandarinkafe.mandarin.features.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.CartItem
import com.mandarinkafe.mandarin.core.domain.models.totalPrice
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.ButtonWithCircularProgressIndicator
import com.mandarinkafe.mandarin.util.presentation.ui.components.UndoIndicator
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.CartControls
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CartControlWithUndo(
    item: CartItem,
    mealInPendingDeletion: Boolean,
    isHidden: Boolean,
    deletionProgress: Float,
    onAddToCart: () -> Unit,
    onRemoveFromCart: () -> Unit,
    onCancel: () -> Unit,
    isInProgress: Boolean,
) {
    when {
        // progress bar, пока обновляется инфо по блюду в корзине
        isInProgress ->
            Box(
                modifier = Modifier
                    .widthIn(min = Dimens.ButtonToCartBig120)
                    .height(Dimens.ButtonToCartSmall36)
                    .clip(RoundedCornerShape(Dimens.CornerRadius8))
                    .background(Colors.OrangeTransparent20)
            ) {
                ButtonWithCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

        // надпись "нет в наличии" в виде неактивной кнопки
        isHidden -> {
            ButtonWithText(
                modifier = Modifier
                    .widthIn(min = Dimens.ButtonToCartBig120)
                    .height(Dimens.ButtonToCartSmall36),
                shouldBeActive = false,
                text = stringResource(MR.strings.item_is_temporary_unavailable),
                onClick = { },
            )
        }

        //  кнопка "отменить удаление"
        mealInPendingDeletion -> {
            UndoIndicator(
                progress = deletionProgress,
                onCancel = onCancel,
            )

        }

        //  обычные кнопки управления товара в корзине
        else -> {
            CartControls(
                modifier = Modifier
                    .widthIn(min = Dimens.ButtonToCartBig120)
                    .height(Dimens.ButtonToCartSmall36),
                numberInCart = item.quantity.toDouble(),
                totalPrice = (item.customizedMeal.totalPrice() * item.quantity).toDouble(),
                onIncrease = onAddToCart,
                onDecrease = onRemoveFromCart,
            )
        }
    }
}
