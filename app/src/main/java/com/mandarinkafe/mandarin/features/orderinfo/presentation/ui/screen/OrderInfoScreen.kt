package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.models.UiError.EmptyOrderData
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEffect
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent.StopObservingStatus
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToCart
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent.OnMealDetailsClick
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import com.mandarinkafe.mandarin.util.presentation.ui.screen.PlaceholderScreen
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderInfoScreen(
    orderID: String?,
    sharedViewModel: SharedViewModel,
    viewModel: OrderInfoViewModel = hiltViewModel(),
    fromOrderCreation: Boolean = false,
    navController: NavHostController,
) {
    if (orderID == null) return
    val onEvent = viewModel::onEvent
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    val onSharedEvent = sharedViewModel::onEvent
    val snackbarHostState = LocalSnackbarHostState.current
    val someItemsUnavailableText = stringResource(R.string.some_items_unavailable)
    val allItemsAddedText = stringResource(R.string.all_items_added_to_cart)

    LaunchedEffect(Unit) {
        onEvent(OrderInfoEvent.SetInitId(orderID))
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = { onEvent(OrderInfoEvent.RefreshNow) }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val title = state.incomingOrder?.let {
                stringResource(
                    R.string.order_info_screen_title_with_number,
                    it.number ?: ""
                )
            } ?: stringResource(R.string.order_info_screen_title)
            ScreenTitleWithBackButton(
                name = title,
                showBackButton = !fromOrderCreation,
                onBackClick = { navController.popBackStack() }
            )

            if (state.incomingOrder != null) {
                OrderInfoContentScreen(
                    order = state.incomingOrder,
                    state = state,
                    onEvent = onEvent,
                    navController = navController,
                    orderRepeatingInProgress = state.orderRepeatingInProgress,
                    fromOrderCreation = fromOrderCreation,
                    onOrderItemClick = { mealId -> onSharedEvent(OnMealDetailsClick(mealId = mealId)) }
                )
            } else {
                PlaceholderScreen(error = EmptyOrderData)
            }
        }
        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

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
