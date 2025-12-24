package com.mandarinkafe.mandarin.util.presentation.ui.components.intents

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun getContextForSettings(): Any? = LocalContext.current

