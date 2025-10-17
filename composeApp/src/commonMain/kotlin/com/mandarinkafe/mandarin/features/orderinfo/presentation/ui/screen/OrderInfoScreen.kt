package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun OrderInfoScreen(
    orderID: String,
    fromOrderCreation: Boolean,
    sharedViewModel: SharedViewModel,
    navigator: Navigator,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Order Info Screen - KMP Migration Placeholder")
    }
}
