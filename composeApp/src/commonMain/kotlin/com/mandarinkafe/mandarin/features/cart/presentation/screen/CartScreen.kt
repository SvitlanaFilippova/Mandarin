package com.mandarinkafe.mandarin.features.cart.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
    navigator: Navigator,
    snackbarMessage: String?

) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Cart Screen - KMP Migration Placeholder")
    }
}
