package com.mandarinkafe.mandarin.core.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.features.cart.ui.view_model.CartViewModel
import com.mandarinkafe.mandarin.navigation.BottomNavigation
import com.mandarinkafe.mandarin.navigation.NavGraph

@Preview
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = hiltViewModel()
    val state by cartViewModel.state.collectAsState()
    val cartCount = state.cartItemsCount

    Scaffold(
        bottomBar = {
            BottomNavigation(
                navController = navController,
                cartCount = cartCount,
                cartIsPendingDeletion = state.isPendingDeletion,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(Colors.AppBackgroundColor)
        ) {
            NavGraph(
                navHostController = navController
            )
        }
    }
}