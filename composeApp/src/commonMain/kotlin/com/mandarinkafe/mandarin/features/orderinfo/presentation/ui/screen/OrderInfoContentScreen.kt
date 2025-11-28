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
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.AddressInfo
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.CustomerInfo
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderActionsButtons
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderItemsSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderProblemSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderStatusSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderTimesSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.OrderTypeSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components.PaymentInfoSection
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoState
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMenu
import com.mandarinkafe.mandarin.util.presentation.ui.components.ClickToCopyText
import com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs.RemoveConfirmationDialog
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

    val shouldShowRefundText = state.paymentStatus == PaymentStatus.REFUNDED

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        item {
            OrderStatusSection(
                deliveryStatus = state.deliveryStatus,
                shouldShowRefundText = shouldShowRefundText,
                isOnlinePayment = state.isOnlinePayment,
                isPaymentPaid = state.isPaymentPaid
            )
        }

        item { OrderProblemSection(order.errorInfo) }

        item { OrderTypeSection(order.orderType) }

        item {
            PaymentInfoSectionItem(
                order = order,
                state = state,
                onEvent = onEvent
            )
        }

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

        item {
            OrderActionsButtons(
                isClosed = order.isClosed,
                hasItems = order.items.isNotEmpty(),
                canBeCanceled = order.canBeCanceled,
                fromOrderCreation = fromOrderCreation,
                onCancelClick = { showCancelDialog = true },
                orderRepeatingInProgress = orderRepeatingInProgress,
                onRepeatOrderCLick = { onEvent(OrderInfoEvent.RepeatOrder) },
                onBackToMenuCLick = { navController.navigateToMenu() },
                onDeleteOrderCLick = { showDeleteDialog = true }
            )
        }

        item {
            OrderIdSection(orderId = order.id)
        }
    }

    CancelOrderDialog(
        showDialog = showCancelDialog,
        onConfirm = {
            showCancelDialog = false
            onEvent(OrderInfoEvent.CancelOrder)
        },
        onDismiss = { showCancelDialog = false }
    )

    DeleteOrderDialog(
        showDialog = showDeleteDialog,
        onConfirm = {
            showDeleteDialog = false
            onEvent(OrderInfoEvent.DeleteOrderFromHistory)
        },
        onDismiss = { showDeleteDialog = false }
    )
}

@Composable
private fun PaymentInfoSectionItem(
    order: IncomingOrder,
    state: OrderInfoState,
    onEvent: (OrderInfoEvent) -> Unit,
) {
    val paymentMethodCode = state.displayPaymentMethodCode
    if (paymentMethodCode == null) return

    val isOnlinePayment = state.isOnlinePayment
    val isOnlinePaymentActive = isOnlinePayment && !order.isClosed

    val canShowPaymentButtonPassed =
        if (isOnlinePaymentActive) state.canShowPaymentButton else false

    PaymentInfoSection(
        paymentStatus = if (isOnlinePayment) state.paymentStatus else null,
        isPaymentInProgress = if (isOnlinePaymentActive) state.isPaymentInProgress else false,
        isPaymentProcessing = if (isOnlinePaymentActive) state.isPaymentProcessing else false,
        isPaymentPolling = if (isOnlinePaymentActive) state.isPaymentPolling else false,
        canShowPaymentError = if (isOnlinePaymentActive) state.canShowPaymentError else false,
        canShowPaymentButton = canShowPaymentButtonPassed,
        paymentError = if (isOnlinePaymentActive) state.paymentError else null,
        paymentMethodCode = paymentMethodCode,
        isChangingPaymentMethod = state.isChangingPaymentMethod,
        paymentCanBeChanged = state.paymentCanBeChanged,
        availablePaymentTypes = state.availablePaymentTypes,
        onStartPayment = { onEvent(OrderInfoEvent.StartPayment) },
        onRetryPayment = { onEvent(OrderInfoEvent.RetryPayment) },
        onLoadPaymentTypes = { onEvent(OrderInfoEvent.LoadPaymentTypesForChange) },
        onChangePaymentMethod = { code ->
            onEvent(OrderInfoEvent.ChangePaymentMethod(code))
        },
        paymentDeadline = if (isOnlinePaymentActive) order.paymentDeadline else null
    )
}

@Composable
private fun OrderIdSection(orderId: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.MarginSmall8),
        contentAlignment = Alignment.Center
    ) {
        ClickToCopyText(
            text = "ID: $orderId",
            style = Typography.ExtraSmallTextStyle,
            color = Colors.LightGrey
        )
    }
}

@Composable
private fun CancelOrderDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (showDialog) {
        RemoveConfirmationDialog(
            title = stringResource(MR.strings.cancel_order_question),
            text = stringResource(MR.strings.cancel_order_confirmation),
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun DeleteOrderDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    if (showDialog) {
        RemoveConfirmationDialog(
            title = stringResource(MR.strings.delete_order_from_history_question),
            text = stringResource(MR.strings.delete_order_from_history_confirmation),
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}
