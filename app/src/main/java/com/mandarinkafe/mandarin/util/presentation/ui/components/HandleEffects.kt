package com.mandarinkafe.mandarin.util.presentation.ui.components

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.core.domain.mapper.Mapper.toCustomizedMeal
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.navigation.NavConstants.MENU_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMealDetails
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.util.Constants.PHONE_NUMBER_DEFAULT
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleEffects(
    effectFlow: Flow<SharedEffect>,
    navController: NavController,
) {
    val context = LocalContext.current

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is SharedEffect.OnPhoneClick -> {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = PHONE_NUMBER_DEFAULT.toUri()
                    }
                    context.startActivity(intent)
                }

                is SharedEffect.OpenMealDetailsBS -> {
                    navController.navigateToMealDetails(
                        meal = effect.item
                            ?: effect.meal?.toCustomizedMeal()
                            ?: return@collect,
                        isEditMode = effect.isEditMode
                    )
                }

                is SharedEffect.NavigateToMain -> {
                    navController.navigate(MENU_SCREEN_ROUTE) {
                        popUpTo(NavConstants.SPLASH_SCREEN_ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}