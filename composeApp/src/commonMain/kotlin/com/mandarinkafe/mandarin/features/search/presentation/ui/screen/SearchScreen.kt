package com.mandarinkafe.mandarin.features.search.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.features.cart.presentation.viewmodel.CartViewModel
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel

@Composable
fun SearchScreen(
    focusSearchBarInput: Boolean,
    cartViewModel: CartViewModel,
    sharedViewModel: SharedViewModel,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Search Screen - KMP Migration Placeholder")
    }
}
