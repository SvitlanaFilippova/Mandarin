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
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.models.UiError.EmptyOrderData
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEffect
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent.StopObservingStatus
import com.mandarinkafe.mandarin.navigation.extensions.navigateToCart
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberOrderInfoViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import kotlinx.coroutines.flow.collectLatest
import androidx.navigation.NavController

@Composable
fun OrderInfoScreen(
    orderID: String?,
    fromOrderCreation: Boolean,
    sharedViewModel: SharedViewModel,
    navController: NavController,
) {
    val viewModel = rememberOrderInfoViewModel()
    if (orderID == null) return

    val onEvent = viewModel::onEvent
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    val onSharedEvent = sharedViewModel::onEvent
    val snackbarHostState = LocalSnackbarHostState.current
    val someItemsUnavailableText = stringResource(MR.strings.some_items_unavailable)
    val allItemsAddedText = stringResource(MR.strings.all_items_added_to_cart)

    LaunchedEffect(Unit) {
        onEvent(OrderInfoEvent.SetInitId(orderID))
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
                showBackButton = !fromOrderCreation,
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
                }
            }
        }
    }
}
