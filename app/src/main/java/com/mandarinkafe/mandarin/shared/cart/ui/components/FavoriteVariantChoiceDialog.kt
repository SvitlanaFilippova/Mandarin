package com.mandarinkafe.mandarin.shared.cart.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R

@Composable
fun FavoriteVariantChoiceDialog(
    onBaseSelected: () -> Unit,
    onCustomSelected: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.favorite_variant_dialog_title))
        },
        text = {
            Text(text = stringResource(R.string.favorite_variant_dialog_message))
        },
        confirmButton = {
            TextButton(onClick = onCustomSelected) {
                Text(text = stringResource(R.string.favorite_variant_custom_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onBaseSelected) {
                Text(text = stringResource(R.string.favorite_variant_base_button))
            }
        }
    )
}