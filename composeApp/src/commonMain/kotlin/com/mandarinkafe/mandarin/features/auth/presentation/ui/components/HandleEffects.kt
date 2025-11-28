package com.mandarinkafe.mandarin.features.auth.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.auth.presentation.viewmodel.AuthContract
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.util.Constants.DELAY_1_SECOND
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import net.thauvin.erik.urlencoder.UrlEncoderUtil

@Composable
fun HandleEffects(
    effectFlow: Flow<AuthContract.AuthEffect>,
    navController: NavController,
    showSuccessDialog: (Boolean) -> Unit,
    targetRoute: String?,
    forDeleteAccount: Boolean = false,
) {
    // Получаем строку в Composable контексте
    val cartUpdatedMessage = stringResource(MR.strings.cart_updated_after_sync)

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                AuthContract.AuthEffect.SuccessAuth -> {
                    if (forDeleteAccount) {
                        // Если это верификация для удаления аккаунта, возвращаемся на экран аккаунта
                        // и передаем сигнал о том, что телефон подтвержден
                        val route =
                            "${NavConstants.ACCOUNT_ROUTE}?${NavConstants.KEY_PHONE_VERIFIED}=true"
                        navController.navigate(route) {
                            popUpTo(NavConstants.AUTH_ROUTE) {
                                inclusive = true
                            }
                        }
                    } else {
                        showSuccessDialog(true)
                        delay(DELAY_1_SECOND)
                        val destination = targetRoute ?: NavConstants.MENU_SCREEN_ROUTE
                        navController.navigate(destination) {
                            popUpTo(NavConstants.AUTH_ROUTE) {
                                inclusive = true // убираем экран авторизации из стека
                            }
                        }
                    }
                }

                AuthContract.AuthEffect.SuccessAuthWithCartChanged -> {
                    if (forDeleteAccount) {
                        // Если это верификация для удаления аккаунта, возвращаемся на экран аккаунта
                        val route =
                            "${NavConstants.ACCOUNT_ROUTE}?${NavConstants.KEY_PHONE_VERIFIED}=true"
                        navController.navigate(route) {
                            popUpTo(NavConstants.AUTH_ROUTE) {
                                inclusive = true
                            }
                        }
                    } else {
                        showSuccessDialog(true)
                        delay(DELAY_1_SECOND)
                        // Редиректим в корзину с сообщением об изменении
                        val encodedMessage = UrlEncoderUtil.encode(cartUpdatedMessage)
                        val route =
                            "${NavConstants.CART_SCREEN_ROUTE}?${NavConstants.KEY_SNACKBAR_MESSAGE}=$encodedMessage"
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
}