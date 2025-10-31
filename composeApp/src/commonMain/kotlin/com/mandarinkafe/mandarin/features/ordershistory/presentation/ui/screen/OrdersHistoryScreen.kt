package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components.FiltersSection
import com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components.OrdersHistoryList
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEffect
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEvent
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedContract
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.SharedViewModel
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberOrdersHistoryViewModel
import com.mandarinkafe.mandarin.util.presentation.LocalSnackbarHostState
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitleWithBackButton
import dev.icerock.moko.resources.compose.stringResource
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun OrdersHistoryScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
) {
    val viewModel = rememberOrdersHistoryViewModel()
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val onEvent = viewModel::onEvent
    val effectFlow = viewModel.effect
    val snackbarHostState = LocalSnackbarHostState.current

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading == true,
        onRefresh = { onEvent(OrdersHistoryEvent.ForceRefresh) }
    )
    LaunchedEffect(Unit) { onEvent(OrdersHistoryEvent.ForceRefresh) }

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
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTitleWithBackButton(
                name = stringResource(MR.strings.order_history),
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
                listState = listState,
                navController = navController,
                fullData = state.fullData,
                filteredData = state.filteredData,
                anyFiltersAreApplied = state.anyFiltersAreApplied
            )
        }

    }

    LaunchedEffect(Unit) {
        launch {
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
        launch {
            sharedViewModel.effect.collect { effect ->
                if (effect is SharedContract.SharedEffect.ScrollToTop) {
                    listState.scrollToItem(0)
                }
            }
        }
    }
}

