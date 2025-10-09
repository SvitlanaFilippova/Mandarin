package com.mandarinkafe.mandarin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    // Пока что простой экран для демонстрации KMP
    Text(
        text = "Mandarin KMP App\nPlatform: ${getPlatform().name}",
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxSize()
    )
}
