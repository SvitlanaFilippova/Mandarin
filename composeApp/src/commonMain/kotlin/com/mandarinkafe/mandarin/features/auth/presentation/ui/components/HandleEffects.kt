package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.navOptions
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthContract
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.util.Constants.DELAY_1_SECOND
import dev.icerock.moko.resources.compose.stringResource
import net.thauvin.erik.urlencoder.UrlEncoderUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HandleEffects(
    effectFlow: Flow<AuthContract.AuthEffect>,
    navController: NavController,
    showSuccessDialog: (Boolean) -> Unit,
    targetRoute: String?,
) {
    // Получаем строку в Composable контексте
    val cartUpdatedMessage = stringResource(MR.strings.cart_updated_after_sync)
    
    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                AuthContract.AuthEffect.SuccessAuth -> {
                    showSuccessDialog(true)
                    delay(DELAY_1_SECOND)
                    val destination = targetRoute ?: NavConstants.MENU_SCREEN_ROUTE
                    navController.navigate(destination) {
                        popUpTo(NavConstants.AUTH_ROUTE) {
                            inclusive = true // убираем экран авторизации из стека
                        }
                    }
                }
                
                AuthContract.AuthEffect.SuccessAuthWithCartChanged -> {
                    showSuccessDialog(true)
                    delay(DELAY_1_SECOND)
                    // Редиректим в корзину с сообщением об изменении
                    val encodedMessage = UrlEncoderUtil.encode(cartUpdatedMessage)
                    val route = "${NavConstants.CART_SCREEN_ROUTE}?${NavConstants.KEY_SNACKBAR_MESSAGE}=$encodedMessage"
                    navController.navigate(route) {
                        popUpTo(NavConstants.AUTH_ROUTE) {
                            inclusive = true // убираем экран авторизации из стека
                        }
                    }
                }
            }
        }
    }
}