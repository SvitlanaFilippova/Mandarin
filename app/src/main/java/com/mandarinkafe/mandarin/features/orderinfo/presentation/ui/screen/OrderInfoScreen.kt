package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.AddressInfo
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.CustomerInfo
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderInfoSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderItemsSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderStatusSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderTimesSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent.StopObservingStatus
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMenu
import com.mandarinkafe.mandarin.util.presentation.ui.components.ConfirmationDialog
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText

@Composable
fun OrderInfoScreen(
    orderID: String?,
    requireConfirmation: Boolean,
    viewModel: OrderInfoViewModel = hiltViewModel(),
    navController: NavHostController
) {
    if (orderID == null) return
    val onEvent = viewModel::onEvent
    val state by viewModel.state.collectAsState()
    Log.d(
        "DEBUG Order info",
        "orderID: $orderID, status:${state.deliveryStatus}, isClosed: ${state.incomingOrder?.isClosed} canBeCanceled:${state.incomingOrder?.canBeCanceled}"
    )
    LaunchedEffect(Unit) {
        onEvent(OrderInfoEvent.SetInitId(orderID))
    }

    val order = state.incomingOrder
    var showCancelDialog by remember { mutableStateOf(false) }

    order?.let {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.MarginSmall8),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(Dimens.MarginSmall8),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    order.number?.let {
                        Text(
                            text = stringResource(
                                R.string.order_number_created,
                                it,
                                order.whenCreated ?: ""
                            ),
                            style = Typography.RegularLightTextStyle
                        )
                    }
                }
            }

            item {
                OrderStatusSection(deliveryStatus = state.deliveryStatus)
            }

            item { OrderInfoSection(order) }

            if (order.items.isNotEmpty()) {
                item {
                    OrderItemsSection(
                        items = order.items,
                        sum = order.sum,
                        discountName = order.discountReason,
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

            // ID заказа
            item {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "ID: ${order.id}",
                        style = Typography.ExtraSmallTextStyle,
                        color = Colors.LightGrey
                    )
                }
            }

            // Кнопки

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    when {
                        order.isClosed -> {
                            ButtonWithText(
                                modifier = Modifier
                                    .padding(Dimens.MarginSmall8)
                                    .weight(1f),
                                textResID = R.string.repeat_order,
                                containerColor = Colors.LabelVegGreen,
                                onClick = { onEvent(OrderInfoEvent.RepeatOrder) }
                            )
                        }

                        order.canBeCanceled -> {
                            ButtonWithText(
                                modifier = Modifier
                                    .padding(Dimens.MarginSmall8)
                                    .weight(1f),
                                textResID = R.string.cancel_order,
                                containerColor = Colors.ErrorRed,
                                onClick = { showCancelDialog = true }

                            )
                        }
                    }

                    ButtonWithText(
                        modifier = Modifier
                            .padding(Dimens.MarginSmall8)
                            .weight(1f),
                        textResID = R.string.back_to_menu,
                        onClick = { navController.navigateToMenu() }
                    )
                }
            }
        }
    }

    // Диалог для подтверждения желания отменить заказ
    if (showCancelDialog) {
        ConfirmationDialog(
            titleRes = R.string.cancel_order_question,
            textRes = R.string.cancel_order_confirmation,
            onConfirm = {
                showCancelDialog = false
                viewModel.onEvent(OrderInfoEvent.CancelOrder)
            },
            onDismiss = {
                showCancelDialog = false
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose { onEvent(StopObservingStatus) }
    }
}
