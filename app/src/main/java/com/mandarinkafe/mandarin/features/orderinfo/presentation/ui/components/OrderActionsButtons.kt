package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.ButtonWithCircularProgressIndicator
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText

@Composable
fun OrderActionsButtons(
    isClosed: Boolean,
    hasItems: Boolean,
    canBeCanceled: Boolean,
    fromOrderCreation: Boolean,
    onCancelClick: () -> Unit,
    orderRepeatingInProgress: Boolean,
    onRepeatOrderCLick: () -> Unit,
    onBackToMenuCLick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        when {
            isClosed && hasItems -> {
                if (orderRepeatingInProgress) {
                    ButtonWithCircularProgressIndicator()
                } else {
                    ButtonWithText(
                        modifier = Modifier
                            .padding(Dimens.MarginSmall8)
                            .weight(1f),
                        textResID = R.string.repeat_order_button,
                        containerColor = Colors.Green,
                        onClick = onRepeatOrderCLick
                    )
                }
            }

            canBeCanceled -> {
                ButtonWithText(
                    modifier = Modifier
                        .padding(Dimens.MarginSmall8)
                        .weight(1f),
                    textResID = R.string.cancel_order_button,
                    containerColor = Colors.ErrorRed,
                    onClick = onCancelClick

                )
            }
        }
        if (fromOrderCreation) {
            ButtonWithText(
                modifier = Modifier
                    .padding(Dimens.MarginSmall8)
                    .weight(1f),
                textResID = R.string.back_to_menu,
                onClick = onBackToMenuCLick
            )
        }
    }
}