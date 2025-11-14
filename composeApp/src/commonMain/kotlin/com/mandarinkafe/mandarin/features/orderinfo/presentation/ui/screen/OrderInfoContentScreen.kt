package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
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
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.PaymentInfoSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoState
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMenu
import com.mandarinkafe.mandarin.util.presentation.ui.components.ClickToCopyText
import com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs.RemoveConfirmationDialog
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun OrderInfoContentScreen(
    order: IncomingOrder?,
    state: OrderInfoState,
    onEvent: (OrderInfoEvent) -> Unit,
    navController: NavController,
    onOpenMealDetails: (String) -> Unit,
    orderRepeatingInProgress: Boolean,
    fromOrderCreation: Boolean,
    showNoLongerInMenuMessage: () -> Unit,
) {
    if (order == null) return
    var showCancelDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
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
                    onOpenMealDetails = onOpenMealDetails,
                    showNoLongerInMenuMessage = showNoLongerInMenuMessage,
                )
            }
        }

        if (state.isOnlinePayment && !order.isClosed) {
            item {
                PaymentInfoSection(
                    paymentStatus = state.paymentStatus,
                    isPaymentPaid = state.isPaymentPaid,
                    isPaymentLoading = state.isPaymentLoading,
                    isPaymentProcessing = state.isPaymentProcessing,
                    isPaymentPolling = state.isPaymentPolling,
                    paymentError = state.paymentError,
                    onStartPayment = { onEvent(OrderInfoEvent.StartPayment) },
                    onRetryPayment = { onEvent(OrderInfoEvent.RetryPayment) }
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
                customerName = order.customerName,
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


        // Кнопка удаления заказа из истории (только для закрытых заказов)
        if (order.isClosed) {
            item {
                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.MarginSmall8),
                    border = BorderStroke(width = Dimens.Border1, color = Colors.Red),
                    shape = RoundedCornerShape(Dimens.CornerRadius8),
                    onClick = { showDeleteDialog = true },
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(MR.images.ic_delete),
                            tint = Colors.Red,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(MR.strings.delete_order_from_history_button),
                            style = Typography.SmallTextStyle,
                            color = Colors.Red,
                        )
                    }
                }
            }
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
        RemoveConfirmationDialog(
            title = stringResource(MR.strings.cancel_order_question),
            text = stringResource(MR.strings.cancel_order_confirmation),
            onConfirm = {
                showCancelDialog = false
                onEvent(OrderInfoEvent.CancelOrder)
            },
            onDismiss = {
                showCancelDialog = false
            }
        )
    }

    // Диалог для подтверждения удаления заказа из истории
    if (showDeleteDialog) {
        RemoveConfirmationDialog(
            title = stringResource(MR.strings.delete_order_from_history_question),
            text = stringResource(MR.strings.delete_order_from_history_confirmation),
            onConfirm = {
                showDeleteDialog = false
                onEvent(OrderInfoEvent.DeleteOrderFromHistory)
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }
}
