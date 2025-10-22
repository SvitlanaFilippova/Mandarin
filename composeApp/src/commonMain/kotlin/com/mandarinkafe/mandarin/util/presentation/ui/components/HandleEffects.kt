package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.cart.domain.Mapper.toCartItem
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.navigation.extensions.navigateToCart
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMealDetails
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.util.presentation.ui.components.intents.MakeCall
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow
import androidx.navigation.NavController

@Composable
fun HandleEffects(
    effectFlow: Flow<SharedEffect>,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
) {
    val toCartButtonText = stringResource(MR.strings.snackbar_to_cart_button)
    val phoneNumber = stringResource(MR.strings.cafe_phone_number)
    var pendingSnackbarRes: StringResource? by remember { mutableStateOf(null) }
    var pendingShowToCart by remember { mutableStateOf(false) }
    var shouldMakePhoneCall by remember { mutableStateOf(false) }
    
    if (shouldMakePhoneCall) {
        MakeCall(
            phoneNumber = phoneNumber,
        )
        LaunchedEffect(Unit) {
            shouldMakePhoneCall = false
        }
    }
    
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is SharedEffect.OpenMealDetailsBS -> {
                    navController.navigateToMealDetails(
                        item = effect.cartItem
                            ?: effect.item?.toCartItem()
                            ?: effect.meal?.toCartItem(),
                        mealId = effect.mealId,
                        isEditMode = effect.isEditMode
                    )
                }

                is SharedEffect.OnPhoneClick -> {
                    shouldMakePhoneCall = true
                }

                is SharedEffect.FinishSplash -> {
                    navController.navigate(
                        route = NavConstants.MENU_SCREEN_ROUTE
                    )
                }

                is SharedEffect.GoBackEffect -> {
                    navController.popBackStack()
                }

                is SharedEffect.SnackbarEffect -> {
                    // сохраняем ресурс и флаг; показ выполнит отдельный LaunchedEffect ниже
                    pendingSnackbarRes = effect.messageRes
                    pendingShowToCart = effect.showToCartButton
                }

                is SharedEffect.ScrollToTop -> {
                    // обрабатывается отдельно
                }
            }
        }
    }

    val pendingMessage: String? = pendingSnackbarRes?.let { stringResource(it) }
    LaunchedEffect(pendingMessage, pendingShowToCart) {
        val message = pendingMessage ?: return@LaunchedEffect
        val actionLabel = if (pendingShowToCart) toCartButtonText else null
        val result = snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short,
            withDismissAction = true,
            actionLabel = actionLabel
        )
        if (result == SnackbarResult.ActionPerformed) {
            navController.navigateToCart()
        }
        pendingSnackbarRes = null
    }
}
