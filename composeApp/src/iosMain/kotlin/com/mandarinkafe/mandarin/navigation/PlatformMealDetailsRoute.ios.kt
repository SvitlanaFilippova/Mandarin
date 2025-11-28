package com.mandarinkafe.mandarin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

actual fun NavGraphBuilder.platformMealDetailsRoute(
    content: @Composable (backStackEntry: NavBackStackEntry) -> Unit,
) {
    composable(
        route = "${NavConstants.MEAL_DETAILS_ROUTE}?" +
                "${NavConstants.KEY_MEAL_ID}={${NavConstants.KEY_MEAL_ID}}&" +
                "${NavConstants.KEY_IS_EDIT_MODE}={${NavConstants.KEY_IS_EDIT_MODE}}",
        arguments = listOf(
            navArgument(NavConstants.KEY_MEAL_ID) {
                type = NavType.StringType
            },
            navArgument(NavConstants.KEY_IS_EDIT_MODE) {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) { backStackEntry ->
        content(backStackEntry)
    }
}
