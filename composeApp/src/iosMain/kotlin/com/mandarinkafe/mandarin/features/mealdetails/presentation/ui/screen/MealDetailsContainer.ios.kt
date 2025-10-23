package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Colors

@Composable
actual fun MealDetailsContainer(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    // На iOS показываем фуллскрин вместо bottom sheet
    if (!visible) {
        LaunchedEffect(Unit) { onDismissRequest() }
        return
    }
    Column(
        modifier = Modifier.fillMaxSize()
            .background(Colors.AppBlack)
    ) {
        content()
    }
}

