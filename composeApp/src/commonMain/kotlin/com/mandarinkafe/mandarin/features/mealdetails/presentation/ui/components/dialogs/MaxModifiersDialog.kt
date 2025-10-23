package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.components.dialogs

import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs.InformationDialog

@Composable
fun MaxModifiersDialog(show: Boolean, onDismiss: () -> Unit) {
    if (show) {
        InformationDialog(
            textRes = MR.strings.maximum_modifier,
            onDismiss = onDismiss
        )
    }
}