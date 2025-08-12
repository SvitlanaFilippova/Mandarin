package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEffect
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoContract.OrderInfoEvent.StopObservingStatus
import com.mandarinkafe.mandarin.features.orderinfo.presentation.viewmodel.OrderInfoViewModel
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent.OnMealDetailsClick
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.LoadingScreen

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

    when {
        state.isLoading -> LoadingScreen()
        state.incomingOrder != null -> {
            OrderInfoContentScreen(
                order = state.incomingOrder,
                state = state,
                onEvent = onEvent,
                navController = navController,
                onOrderItemClick = { mealId -> onSharedEvent(OnMealDetailsClick(mealId = mealId)) }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose { onEvent(StopObservingStatus) }
    }


    LaunchedEffect(effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is OrderInfoEffect.ShowError -> onSharedEvent(
                    SharedEvent.ShowSnackbar(
                        text = effect.message
                    )
                )
            }
        }
    }
}
