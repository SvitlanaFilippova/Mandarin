package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.core.presentation.theme.Colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun MealDetailsContainer(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (!visible) return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        containerColor = Colors.AppBlack,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        content = {
            Column {
                content()
            }
        }
    )
}

