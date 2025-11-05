package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthContract
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.util.Constants.DELAY_1_SECOND
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HandleEffects(
    effectFlow: Flow<AuthContract.AuthEffect>,
    navController: NavController,
    showSuccessDialog: (Boolean) -> Unit,
    onRequestAlreadyActive: (remainingSeconds: Int) -> Unit,
    targetRoute: String?,
) {
    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                AuthContract.AuthEffect.SuccessAuth -> {
                    showSuccessDialog(true)
                    delay(DELAY_1_SECOND)
                    showSuccessDialog(false)
                    val destination = targetRoute ?: NavConstants.MENU_SCREEN_ROUTE
                    navController.navigate(destination) {
                        popUpTo(NavConstants.AUTH_ROUTE) {
                            inclusive = true // убираем экран авторизации из стека
                        }
                    }
                }

                is AuthContract.AuthEffect.RequestAlreadyActive -> onRequestAlreadyActive(effect.remainingSeconds)
            }
        }
    }
}