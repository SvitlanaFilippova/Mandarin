package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun OrdersHistoryScreen(
    navigator: Navigator,
    sharedViewModel: SharedViewModel,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Orders History Screen - KMP Migration Placeholder")
    }
}
