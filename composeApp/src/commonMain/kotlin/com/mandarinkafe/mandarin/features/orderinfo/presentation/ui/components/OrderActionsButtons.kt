package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.ui.components.ButtonWithCircularProgressIndicator
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun OrderActionsButtons(
    isClosed: Boolean,
    hasItems: Boolean,
    canBeCanceled: Boolean,
    fromOrderCreation: Boolean,
    onCancelClick: () -> Unit,
    orderRepeatingInProgress: Boolean,
    onRepeatOrderCLick: () -> Unit,
    onDeleteOrderCLick: () -> Unit,
    onBackToMenuCLick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.MarginSmall8),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.MarginSmall8),
            horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            when {
                isClosed && hasItems -> {
                    if (orderRepeatingInProgress) {
                        ButtonWithCircularProgressIndicator(
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Colors.Green,
                            ),
                            shape = RoundedCornerShape(Dimens.CornerRadius8),
                            onClick = onRepeatOrderCLick,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(MR.images.ic_repeat),
                                    tint = Colors.White,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.size(Dimens.MarginSmall8))
                                Text(
                                    text = stringResource(MR.strings.repeat_order_button),
                                    style = Typography.SmallTextStyle,
                                    color = Colors.White,
                                )
                            }
                        }
                    }
                }

                canBeCanceled -> {
                    ButtonWithText(
                        modifier = Modifier.weight(1f),
                        text = stringResource(MR.strings.cancel_order_button),
                        containerColor = Colors.Red,
                        onClick = onCancelClick

                    )
                }
            }
            if (fromOrderCreation) {
                ButtonWithText(
                    modifier = Modifier.weight(1f),
                    text = stringResource(MR.strings.back_to_menu),
                    onClick = onBackToMenuCLick
                )
            }
        }

        // Кнопка удаления заказа из истории (только для закрытых заказов)
        if (isClosed) {
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimens.MarginSmall8,
                    ),
                border = BorderStroke(width = Dimens.Border1, color = Colors.Red),
                shape = RoundedCornerShape(Dimens.CornerRadius8),
                onClick = onDeleteOrderCLick,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(MR.images.ic_delete),
                        tint = Colors.Red,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(Dimens.MarginSmall8))
                    Text(
                        text = stringResource(MR.strings.delete_order_from_history_button),
                        style = Typography.SmallTextStyle,
                        color = Colors.Red,
                    )
                }
            }
        }
    }
}