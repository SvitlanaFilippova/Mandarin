package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddress
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddressDetails
import com.mandarinkafe.mandarin.navigation.extensions.navigateToOrderInfo
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HandleOrderEffects(
    effectFlow: Flow<OrderEffect>,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
) {
    var pendingMessageRes: StringResource? by remember { mutableStateOf(null) }
    var pendingDetails: String? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is OrderEffect.AddNewAddress ->
                    navController.navigateToAddress(
                        returnToRoute = NavConstants.ORDER_SCREEN_ROUTE
                    )

                is OrderEffect.EditAddress ->
                    navController.navigateToAddressDetails(
                        effect.address,
                        isEditMode = true,
                        returnToRoute = NavConstants.ORDER_SCREEN_ROUTE
                    )

                is OrderEffect.ShowMessage -> {
                    pendingMessageRes = effect.message
                    pendingDetails = effect.details
                }

                is OrderEffect.ShowOrderClosingDialog -> Unit

                is OrderEffect.ShowSuccess ->
                    navController.navigateToOrderInfo(
                        orderId = effect.orderId,
                        fromOrderCreation = true,
                        paymentMethodCode = effect.paymentMethodCode
                    )

                is OrderEffect.StartOnlinePayment -> {
                    // Переходим на экран информации о заказе, где будет запущена оплата
                    navController.navigateToOrderInfo(
                        orderId = effect.orderId,
                        fromOrderCreation = true,
                        paymentMethodCode = effect.paymentMethodCode
                    )
                }
            }
        }
    }

    val pendingMessage: String? = pendingMessageRes?.let { stringResource(it) }

    LaunchedEffect(pendingMessage, pendingDetails) {
        val msg = pendingMessage ?: return@LaunchedEffect
        val fullMessage = if (pendingDetails != null) {
            "$msg\n$pendingDetails"
        } else {
            msg
        }
        snackbarHostState.showSnackbar(fullMessage)
        pendingMessageRes = null
        pendingDetails = null
    }
}