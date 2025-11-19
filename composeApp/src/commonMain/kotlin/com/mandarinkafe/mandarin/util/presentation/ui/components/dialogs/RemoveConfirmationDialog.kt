package com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun RemoveConfirmationDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        containerColor = Colors.DarkGrey,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(MR.strings.remove_yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(MR.strings.remove_no))
            }
        }
    )
}
