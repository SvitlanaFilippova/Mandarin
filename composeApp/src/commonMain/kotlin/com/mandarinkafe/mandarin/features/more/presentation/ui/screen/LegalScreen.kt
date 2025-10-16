package com.mandarinkafe.mandarin.features.more.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel

@Composable
fun LegalScreen(
    onBackClick: () -> Unit,
    onSharedEvent: (SharedContract.SharedEvent) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Legal Screen - KMP Migration Placeholder")
    }
}
