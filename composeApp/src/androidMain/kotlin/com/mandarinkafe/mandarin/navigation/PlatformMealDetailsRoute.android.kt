package com.mandarinkafe.mandarin.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.dialog
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel

actual fun NavGraphBuilder.platformMealDetailsRoute(
    content: @Composable (backStackEntry: NavBackStackEntry) -> Unit
) {
    dialog(
        route = "${NavConstants.MEAL_DETAILS_ROUTE}?" +
                "${NavConstants.KEY_MEAL_JSON}={${NavConstants.KEY_MEAL_JSON}}&" +
                "${NavConstants.KEY_MEAL_ID}={${NavConstants.KEY_MEAL_ID}}&" +
                "${NavConstants.KEY_IS_EDIT_MODE}={${NavConstants.KEY_IS_EDIT_MODE}}"
    ) { backStackEntry ->
        content(backStackEntry)
    }
}
