package com.mandarinkafe.mandarin.util.presentation.ui.components

import android.content.Intent
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.features.cart.data.CartMapper.toCartItem
import com.mandarinkafe.mandarin.navigation.NavConstants.MAIN_GRAPH
import com.mandarinkafe.mandarin.navigation.NavConstants.SPLASH_SCREEN_ROUTE
import com.mandarinkafe.mandarin.navigation.extensions.navigateToCart
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMealDetails
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.util.Constants.PHONE_NUMBER_DEFAULT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun HandleEffects(
    effectFlow: Flow<SharedEffect>,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current

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
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = PHONE_NUMBER_DEFAULT.toUri()
                    }
                    context.startActivity(intent)
                }

                is SharedEffect.FinishSplash -> {
                    navController.navigate(MAIN_GRAPH) {
                        popUpTo(SPLASH_SCREEN_ROUTE) { inclusive = true }
                    }
                }

                is SharedEffect.GoBackEffect -> {
                    navController.popBackStack()
                }

                is SharedEffect.SnackbarEffect -> {
                    // показываем snackbar асинхронно — не блокируем поток collect
                    launch {
                        val actionLabel = if (effect.showToCartButton) "В корзину" else null
                        val result = snackbarHostState.showSnackbar(
                            message = effect.text,
                            duration = SnackbarDuration.Short,
                            withDismissAction = true,
                            actionLabel = actionLabel
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            navController.navigateToCart()
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