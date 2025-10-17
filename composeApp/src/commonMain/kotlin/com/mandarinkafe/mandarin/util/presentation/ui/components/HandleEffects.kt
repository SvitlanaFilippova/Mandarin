package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.cart.domain.Mapper.toCartItem
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.navigation.NavConstants.SPLASH_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.extensions.navigateToCart
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMealDetails
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEffect
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun HandleEffects(
    effectFlow: Flow<SharedEffect>,
    navigator: Navigator,
    snackbarHostState: SnackbarHostState,
    onPhoneClick: () -> Unit,
) {
    val toCartButtonText = stringResource(MR.strings.snackbar_to_cart_button)
    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is SharedEffect.OpenMealDetailsBS -> {
                    navigator.navigateToMealDetails(
                        item = effect.cartItem
                            ?: effect.item?.toCartItem()
                            ?: effect.meal?.toCartItem(),
                        mealId = effect.mealId,
                        isEditMode = effect.isEditMode
                    )
                }

                is SharedEffect.OnPhoneClick -> {
                    onPhoneClick()
                }

                is SharedEffect.FinishSplash -> {
                    navigator.navigate(
                        route = NavConstants.MENU_SCREEN_ROUTE
                    )
                }

                is SharedEffect.GoBackEffect -> {
                    navigator.goBack()
                }

                is SharedEffect.SnackbarEffect -> {
                    // показываем snackbar асинхронно — не блокируем поток collect
                    launch {
                        val actionLabel = if (effect.showToCartButton) toCartButtonText else null
                        val result = snackbarHostState.showSnackbar(
                            message = effect.text,
                            duration = SnackbarDuration.Short,
                            withDismissAction = true,
                            actionLabel = actionLabel
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            navigator.navigateToCart()
                        }
                    }
                }

                is SharedEffect.ScrollToTop -> {
                    // обрабатывается отдельно
                }
            }
        }
    }
}
