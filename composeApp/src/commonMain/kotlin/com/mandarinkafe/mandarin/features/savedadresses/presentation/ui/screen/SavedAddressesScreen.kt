package com.mandarinkafe.mandarin.features.savedadresses.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun SavedAddressesScreen(
    navigator: Navigator,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Saved Addresses Screen - KMP Migration Placeholder")
    }
}
