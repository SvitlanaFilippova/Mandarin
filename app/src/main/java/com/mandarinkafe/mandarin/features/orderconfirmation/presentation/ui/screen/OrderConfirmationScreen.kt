package com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components.CustomerInfo
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components.OrderInfoSection
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components.OrderItemsSection
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components.OrderNeedConfirmSection
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components.OrderStatusSection
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components.OrderTimesSection
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.viewmodel.OrderConfirmationContract.OrderConfirmationEvent
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.viewmodel.OrderConfirmationContract.OrderConfirmationEvent.StopObservingStatus
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.viewmodel.OrderConfirmationViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMenu
import com.mandarinkafe.mandarin.util.presentation.ui.components.AddressInfo
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText

@Composable
fun OrderConfirmationScreen(
    orderID: String?,
    requireConfirmation: Boolean,
    viewModel: OrderConfirmationViewModel = hiltViewModel(),
    navController: NavHostController
) {
    if (orderID == null) return

    val onEvent = viewModel::onEvent
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        onEvent(OrderConfirmationEvent.SetInitId(orderID))
    }

    val order = state.incomingOrder

    order?.let {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.MarginSmall8),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {

            item { OrderInfoSection(order) }

            item { OrderStatusSection(order) }

            if (order.needToConfirm && requireConfirmation) {
                item {
                    OrderNeedConfirmSection()
                }
            }

            if (order.items.isNotEmpty()) {
                item {
                    OrderItemsSection(
                        items = order.items,
                        sum = order.sum
                    )
                }
            }

            if (order.isDelivery) {
                item { AddressInfo(address = order.deliveryAddress) }
            }

            item {
                CustomerInfo(
                    phone = order.phone,
                    comment = order.comment,
                    customerName = order.customer?.name,
                )
            }

            item { OrderTimesSection(order) }

            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ButtonWithText(
                        textResID = R.string.back_to_menu,
                        onClick = { navController.navigateToMenu() }
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { onEvent(StopObservingStatus) }
    }
}
