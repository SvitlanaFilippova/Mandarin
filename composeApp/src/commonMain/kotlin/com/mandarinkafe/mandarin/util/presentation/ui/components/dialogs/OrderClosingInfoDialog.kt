package com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Typography.TitleStyle
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun OrderClosingInfoDialog(
    isClosedForWholeDay: Boolean,
    closingTime: String?,
    onScheduleAnotherDay: () -> Unit,
    onDismiss: () -> Unit,
) {
    val message = if (isClosedForWholeDay) {
        stringResource(MR.strings.error_cafe_closed_today_hint)
    } else {
        stringResource(MR.strings.error_we_are_closing_cart_hint)
    }

    val title = if (isClosedForWholeDay) {
        stringResource(MR.strings.error_cafe_closed_today_title)
    } else {
        stringResource(MR.strings.error_we_are_closing_cart_title, closingTime.orEmpty())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title, style = TitleStyle)
        },
        text = {
            Text(message)
        },
        containerColor = Colors.DarkGrey,
        confirmButton = {
            TextButton(onClick = onScheduleAnotherDay) {
                Text(stringResource(MR.strings.order_schedule_for_another_day))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(MR.strings.cancel))
            }
        },
    )
}
