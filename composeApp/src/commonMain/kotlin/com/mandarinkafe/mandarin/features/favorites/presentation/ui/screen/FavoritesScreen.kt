package com.mandarinkafe.mandarin.features.favorites.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel

@Composable
fun FavoritesScreen(
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Favorites Screen - KMP Migration Placeholder")
    }
}
