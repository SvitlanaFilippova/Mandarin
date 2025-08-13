package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEffect
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent.StopObservingStatus
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoViewModel
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent.OnMealDetailsClick
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderInfoScreen(
    orderID: String?,
    requireConfirmation: Boolean,
    sharedViewModel: SharedViewModel,
    viewModel: OrderInfoViewModel = hiltViewModel(),
    navController: NavHostController
) {
    if (orderID == null) return
    val onEvent = viewModel::onEvent
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    val onSharedEvent = sharedViewModel::onEvent

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
        if (state.incomingOrder != null) {
            OrderInfoContentScreen(
                order = state.incomingOrder,
                state = state,
                onEvent = onEvent,
                navController = navController,
                onOrderItemClick = { mealId -> onSharedEvent(OnMealDetailsClick(mealId = mealId)) }
            )
        }

        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        DisposableEffect(Unit) {
            onDispose { onEvent(StopObservingStatus) }
        }


        LaunchedEffect(effectFlow) {
            effectFlow.collect { effect ->
                when (effect) {
                    is OrderInfoEffect.ShowError -> onSharedEvent(
                        SharedEvent.ShowSnackbar(
                            message = effect.message
                        )
                    )
                }
            }
        }
    }
}
