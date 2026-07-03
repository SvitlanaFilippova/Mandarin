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
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.cart.domain.Mapper.toCartItem
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.navigation.extensions.navigateToCart
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMealDetails
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract.SharedEffect
import com.mandarinkafe.mandarin.util.presentation.ui.components.intents.MakeCall
import com.mandarinkafe.mandarin.util.presentation.ui.components.intents.OpenUrl
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleEffects(
    effectFlow: Flow<SharedEffect>,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
) {
    val toCartButtonText = stringResource(MR.strings.snackbar_to_cart_button)
    var pendingSnackbarRes: StringResource? by remember { mutableStateOf(null) }
    var pendingSnackbarMessage: String? by remember { mutableStateOf(null) }
    var pendingShowToCart by remember { mutableStateOf(false) }
    var shouldMakeCallToPhone: String? by remember { mutableStateOf(null) }
    val telegramUrl = stringResource(MR.strings.telegram_url)
    val whatsappUrl = stringResource(MR.strings.whatsapp_url)
    var urlToOpen by remember { mutableStateOf<String?>(null) }

    urlToOpen?.let { url ->
        OpenUrl(
            url = url,
            onFail = {
                // покажем снекбар «Не удалось открыть ссылку»
                pendingSnackbarRes = MR.strings.cannot_open_link
                pendingSnackbarMessage = null
                pendingShowToCart = false
            }
        )
        LaunchedEffect(Unit) {
            urlToOpen = null
        }
    }

    shouldMakeCallToPhone?.let {
        MakeCall(
            phoneNumber = it,
        )
        LaunchedEffect(Unit) {
            shouldMakeCallToPhone = null
        }
    }

    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            processSharedEffect(
                effect = effect,
                navController = navController,
                setPendingRes = { pendingSnackbarRes = it },
                setPendingMessage = { pendingSnackbarMessage = it },
                setPendingShowToCart = { pendingShowToCart = it },
                setShouldMakePhoneCall = { shouldMakeCallToPhone = it },
                setUrlToOpen = { urlToOpen = it },
                telegramUrl = telegramUrl,
                whatsappUrl = whatsappUrl,
            )
        }
    }

    val pendingMessageFromRes: String? = pendingSnackbarRes?.let { stringResource(it) }
    val pendingMessage: String? = pendingSnackbarMessage ?: pendingMessageFromRes

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
        pendingSnackbarMessage = null
    }
}

private fun processSharedEffect(
    effect: SharedEffect,
    navController: NavController,
    setPendingRes: (StringResource?) -> Unit,
    setPendingMessage: (String?) -> Unit,
    setPendingShowToCart: (Boolean) -> Unit,
    setShouldMakePhoneCall: (String) -> Unit,
    setUrlToOpen: (String) -> Unit,
    telegramUrl: String,
    whatsappUrl: String,
) {
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
            setShouldMakePhoneCall(effect.phoneToCall)
        }

        is SharedEffect.FinishSplash -> {
            // При холодном старте NavHost может ещё не успеть установить graph.
            if (navController.currentDestination == null) return
            navController.navigate(
                route = NavConstants.MENU_SCREEN_ROUTE
            ) {
                popUpTo(NavConstants.SPLASH_SCREEN_ROUTE) { inclusive = true }
            }
        }

        is SharedEffect.GoBackEffect -> {
            navController.popBackStack()
        }

        is SharedEffect.SnackbarEffect -> {
            // сохраняем ресурс/сообщение и флаг; показ выполнит отдельный LaunchedEffect в Compose
            setPendingRes(effect.messageRes)
            setPendingMessage(effect.message)
            setPendingShowToCart(effect.showToCartButton)
        }


        is SharedEffect.OnTelegramClick -> {
            setUrlToOpen(telegramUrl)
        }

        is SharedEffect.OnWhatsappClick -> {
            setUrlToOpen(whatsappUrl)
        }

        is SharedEffect.ScrollToTop -> {
            // обрабатывается отдельно;
        }

    }
}
