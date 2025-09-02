package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components.FiltersSection
import com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components.OrdersHistoryList
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEffect
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEvent
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrdersHistoryScreen(
    navController: NavHostController,
    viewModel: OrdersHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    val effectFlow = viewModel.effect
    val snackbarHostState = LocalSnackbarHostState.current

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading == true,
        onRefresh = { onEvent(OrdersHistoryEvent.ForceRefresh) }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitleWithBackButton(
                name = stringResource(R.string.order_history),
                onBackClick = { navController.popBackStack() }
            )

            FiltersSection(
                chosenOrderTypes = state.chosenOrderTypes,
                chosenDateFilter = state.chosenDateFilterType,
                chosenDateRange = state.chosenDateRange,
                onOrderTypesChange = { onEvent(OrdersHistoryEvent.SetChosenOrderTypes(it)) },
                onDateFilterChange = { onEvent(OrdersHistoryEvent.SetChosenDateFilter(it)) },
                onCustomRangeChange = { onEvent(OrdersHistoryEvent.SetChosenDateRange(it)) },
            )

            OrdersHistoryList(
                navController = navController,
                fullData = state.fullData,
                filteredData = state.filteredData,
                anyFiltersAreApplied = state.anyFiltersAreApplied
            )
        }

        PullRefreshIndicator(
            refreshing = state.isLoading == true,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is OrdersHistoryEffect.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        duration = SnackbarDuration.Long,
                        withDismissAction = true,
                    )
                }
            }
        }
    }
}
