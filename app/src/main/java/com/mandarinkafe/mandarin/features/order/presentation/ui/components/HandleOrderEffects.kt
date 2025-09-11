package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.features.order.presentation.viewmodel.OrderContract.OrderEffect
import com.mandarinkafe.mandarin.navigation.NavConstants
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddress
import com.mandarinkafe.mandarin.navigation.extensions.navigateToAddressDetails
import com.mandarinkafe.mandarin.navigation.extensions.navigateToOrderInfo
import com.mandarinkafe.mandarin.util.asString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HandleOrderEffects(
    effectFlow: Flow<OrderEffect>,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
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

                is OrderEffect.ShowMassage ->
                    snackbarHostState.showSnackbar(effect.message.asString(context))

                is OrderEffect.ShowSuccess ->
                    navController.navigateToOrderInfo(
                        orderId = effect.orderId,
                        fromOrderCreation = true
                    )
            }
        }
    }
}