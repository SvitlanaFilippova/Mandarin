package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.MR
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun InformationDialog(
    textRes: StringResource,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Text(stringResource(textRes))
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(MR.strings.ok))
            }
        }
    )
}
