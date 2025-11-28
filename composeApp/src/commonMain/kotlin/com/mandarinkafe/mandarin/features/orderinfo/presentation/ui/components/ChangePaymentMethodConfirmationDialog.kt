package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ChangePaymentMethodConfirmationDialog(
    paymentMethodName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(MR.strings.change_payment_method_dialog_title)) },
        text = {
            Text(stringResource(MR.strings.change_payment_method_dialog_message, paymentMethodName))
        },
        containerColor = Colors.DarkGrey,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(MR.strings.yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(MR.strings.no_cancel))
            }
        }
    )
}


