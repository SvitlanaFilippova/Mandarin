package com.mandarinkafe.mandarin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry

expect fun androidx.navigation.NavGraphBuilder.platformMealDetailsRoute(
    content: @Composable (backStackEntry: NavBackStackEntry) -> Unit,
)
