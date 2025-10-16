package com.mandarinkafe.mandarin.features.address.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun AddressMapScreen(
    navigator: Navigator,
    initAddress: Any?,
    returnToRoute: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Address Map Screen - KMP Migration Placeholder")
    }
}
