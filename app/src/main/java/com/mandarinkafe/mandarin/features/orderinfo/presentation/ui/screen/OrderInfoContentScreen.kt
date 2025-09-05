package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.AddressInfo
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.CustomerInfo
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderActionsButtons
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderInfoSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderItemsSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderStatusSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderTimesSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoState
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMenu
import com.mandarinkafe.mandarin.util.presentation.ui.components.ClickToCopyText
import com.mandarinkafe.mandarin.util.presentation.ui.components.ConfirmationDialog
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton

@Composable
fun OrderInfoContentScreen(
    order: IncomingOrder?,
    state: OrderInfoState,
    onEvent: (OrderInfoEvent) -> Unit,
    navController: NavHostController,
    onOrderItemClick: (String) -> Unit,
    orderRepeatingInProgress: Boolean,
    fromOrderCreation: Boolean,
) {
    if (order == null) return
    var showCancelDialog by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        item {
            ScreenTitleWithBackButton(
                name = stringResource(
                    R.string.order_info_screen_title_with_number,
                    order.number ?: ""
                ),
                showBackButton = !fromOrderCreation,
                onBackClick = { navController.popBackStack() }
            )
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
                    onOrderItemClick = onOrderItemClick
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

        // Кнопки
        item {
            OrderActionsButtons(
                isClosed = order.isClosed,
                hasItems = order.items.isNotEmpty(),
                canBeCanceled = order.canBeCanceled,
                fromOrderCreation = fromOrderCreation,
                onCancelClick = { showCancelDialog = true },
                orderRepeatingInProgress = orderRepeatingInProgress,
                onRepeatOrderCLick = { onEvent(OrderInfoEvent.RepeatOrder) },
                onBackToMenuCLick = { navController.navigateToMenu() }
            )
        }

        // ID заказа
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.MarginSmall8),
                contentAlignment = Alignment.Center
            ) {
                ClickToCopyText(
                    text = "ID: ${order.id}",
                    style = Typography.ExtraSmallTextStyle,
                    color = Colors.LightGrey
                )
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
                onEvent(OrderInfoEvent.CancelOrder)
            },
            onDismiss = {
                showCancelDialog = false
            }
        )
    }
}
