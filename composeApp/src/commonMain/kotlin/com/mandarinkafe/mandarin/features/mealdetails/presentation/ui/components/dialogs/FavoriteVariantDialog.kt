package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.dialogs

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.features.cart.presentation.components.FavoriteVariantChoiceDialog

@Composable
fun FavoriteVariantDialog(
    show: Boolean,
    onBaseSelected: () -> Unit,
    onCustomSelected: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (show) {
        FavoriteVariantChoiceDialog(
            onBaseSelected = {
                onBaseSelected()
                onDismiss()
            },
            onCustomSelected = {
                onCustomSelected()
                onDismiss()
            },
            onDismiss = onDismiss
        )
    }
}