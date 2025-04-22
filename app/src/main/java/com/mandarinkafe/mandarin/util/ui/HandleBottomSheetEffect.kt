package com.mandarinkafe.mandarin.util.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow

@Composable
fun <T : BottomSheetEffect> HandleBottomSheetEffect(
    effectFlow: Flow<Any>,
    cast: (Any) -> T?,
    sheetContent: @Composable (T, onDismiss: () -> Unit) -> Unit
) {
    var currentEffect by remember { mutableStateOf<T?>(null) }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            cast(effect)?.let { typedEffect ->
                currentEffect = typedEffect
            }
        }
    }

    currentEffect?.let {
        sheetContent(it) {
            currentEffect = null
        }
    }
}