package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.models.UiError.EmptyOrderData
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEffect
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent.StopObservingStatus
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoState
import com.mandarinkafe.mandarin.features.payment.domain.models.PaymentStatus
import com.mandarinkafe.mandarin.navigation.extensions.navigateToCart
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberOrderInfoViewModel
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OrderInfoScreen(
    orderID: String?,
    fromOrderCreation: Boolean,
    paymentMethodCode: String?,
    sharedViewModel: SharedViewModel,
    navController: NavController,
) {
    if (orderID == null) return
    val viewModel = rememberOrderInfoViewModel()

    val onEvent = viewModel::onEvent
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    val onSharedEvent = sharedViewModel::onEvent
    val snackbarHostState = LocalSnackbarHostState.current
    val someItemsUnavailableText = stringResource(MR.strings.some_items_unavailable)
    val allItemsAddedText = stringResource(MR.strings.all_items_added_to_cart)

    LaunchedEffect(orderID, paymentMethodCode) {
        onEvent(OrderInfoEvent.SetInitData(orderID, paymentMethodCode))
    }

    // Автоматически запускаем оплату, если:
    // 1. Заказ только что создан и это онлайн-оплата
    // 2. ИЛИ заказ не оплачен, это онлайн-оплата, и заказ не закрыт
    LaunchedEffect(
        fromOrderCreation,
        state.isOnlinePayment,
        state.incomingOrder,
        state.isPaymentPaid
    ) {
        if (shouldAutoStartPayment(fromOrderCreation, state)) {
            // Небольшая задержка, чтобы экран успел загрузиться
            delay(Constants.DELAY_FOR_UI_RENDERING)
            // Запускаем оплату только если она еще не запущена
            if (canStartPayment(state)) {
                onEvent(OrderInfoEvent.StartPayment)
            }
        }
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { onEvent(OrderInfoEvent.RefreshNow) }
    )
    PullRefreshLayout(
        modifier = Modifier.fillMaxSize(),
        state = pullRefreshState,
        indicator = {
            PullRefreshIndicator(
                state = pullRefreshState,
                contentColor = Colors.Orange,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val title = state.incomingOrder?.let {
                stringResource(
                    MR.strings.order_info_screen_title_with_number,
                    it.number ?: ""
                )
            } ?: stringResource(MR.strings.order_info_screen_title)
            ScreenTitleWithBackButton(
                name = title,
                onBackClick = { navController.popBackStack() }
            )

            when {
                state.incomingOrder != null -> {
                    OrderInfoContentScreen(
                        order = state.incomingOrder,
                        state = state,
                        onEvent = onEvent,
                        navController = navController,
                        orderRepeatingInProgress = state.orderRepeatingInProgress,
                        fromOrderCreation = fromOrderCreation,
                        onOpenMealDetails = { mealId ->
                            onSharedEvent(
                                SharedContract.SharedEvent.OnMealDetailsClick(
                                    mealId = mealId
                                )
                            )
                        },
                        showNoLongerInMenuMessage = {
                            onSharedEvent(
                                SharedContract.SharedEvent.ShowSnackbar(
                                    MR.strings.item_is_no_longer_available,
                                )
                            )
                        }
                    )
                }

                state.incomingOrder == null && !state.isLoading -> {
                    PlaceholderScreen(error = EmptyOrderData)
                }

            }
        }

        DisposableEffect(Unit) {
            onDispose { onEvent(StopObservingStatus) }
        }

        LaunchedEffect(Unit) {
            effectFlow.collectLatest { effect ->
                when (effect) {
                    is OrderInfoEffect.ShowError -> {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Long,
                            withDismissAction = true,
                        )
                    }


                    is OrderInfoEffect.RepeatOrder -> {
                        val message = if (effect.hasInvalidItems) {
                            someItemsUnavailableText
                        } else {
                            allItemsAddedText
                        }
                        navController.navigateToCart(message)
                    }

                    is OrderInfoEffect.NavigateBack -> {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

private fun shouldAutoStartPayment(
    fromOrderCreation: Boolean,
    state: OrderInfoState,
): Boolean {
    // Проверяем базовые условия: онлайн-оплата и заказ не закрыт
    if (!state.isOnlinePayment ||
        state.incomingOrder == null ||
        state.incomingOrder.isClosed
    ) {
        return false
    }

    // Запускаем оплату если:
    // 1. Заказ только что создан
    // 2. ИЛИ заказ не оплачен (isPaymentPaid != true и paymentStatus != SUCCEEDED)
    return fromOrderCreation ||
            state.isPaymentPaid != true && state.paymentStatus != PaymentStatus.SUCCEEDED
}

private fun canStartPayment(state: OrderInfoState): Boolean {
    return !state.isPaymentLoading &&
            !state.isPaymentProcessing &&
            !state.isPaymentPolling &&
            state.isPaymentPaid != true &&
            state.incomingOrder?.isClosed != true
}
