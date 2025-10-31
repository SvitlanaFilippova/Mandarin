package com.mandarinkafe.mandarin.util.presentation.ui.components.intents

import androidx.compose.runtime.Composable

@Composable
expect fun OpenUrl(url: String, onFail: () -> Unit = {})