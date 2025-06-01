package com.mandarinkafe.mandarin.features.meal_details.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R

@Composable
fun RequiredModifiersDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Text(stringResource(R.string.make_mandatory_choice_before_cart))
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.ok))
            }
        }
    )
}