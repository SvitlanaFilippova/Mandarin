package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.runtime.Composable

@Composable
expect fun KmpModalBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
)
