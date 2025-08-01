package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R

@Composable
fun InformationDialog(textRes: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Text(stringResource(textRes))
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.ok))
            }
        }
    )
}