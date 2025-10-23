package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ReplaceOrAddDialog(
    message: String,
    mealName: String? = null,
    onDismiss: () -> Unit,
    onAddNew: () -> Unit,
    onReplace: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(MR.strings.replace_or_add_title)) },
        text = { 
            Text(
                if (mealName != null) {
                    stringResource(MR.strings.replace_or_add_message, mealName)
                } else {
                    message
                }
            ) 
        },
        confirmButton = {
            TextButton(onClick = onReplace) {
                Text(stringResource(MR.strings.replace_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onAddNew) {
                Text(stringResource(MR.strings.add_one_more_button))
            }
        }
    )
}