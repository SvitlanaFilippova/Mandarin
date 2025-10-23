package com.mandarinkafe.mandarin.features.mealdetails.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Colors

@Composable
actual fun MealDetailsContainer(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    // На iOS показываем фуллскрин вместо bottom sheet
        Column(
            modifier = Modifier.fillMaxSize()
                .background(Colors.AppBlack)
        ) {
            content()
        }
    }
