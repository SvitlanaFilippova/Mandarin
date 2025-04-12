package com.mandarinkafe.mandarin.core.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentManager
import androidx.navigation.compose.rememberNavController
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.navigation.BottomNavigation
import com.mandarinkafe.mandarin.navigation.NavGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(fragmentManager: FragmentManager) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(Colors.AppBackgroundColor)
        ) {
              NavGraph(
                  navHostController = navController,
                  fragmentManager = fragmentManager
              )
        }
    }
}