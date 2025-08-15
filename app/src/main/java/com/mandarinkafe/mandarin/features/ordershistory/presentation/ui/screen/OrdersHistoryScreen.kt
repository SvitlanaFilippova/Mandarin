package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components.FiltersSection
import com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components.OrderHistoryCard
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryContract.OrdersHistoryEvent
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToOrderInfo
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitle
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrdersHistoryScreen(
    navController: NavHostController,
    viewModel: OrdersHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent

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
            ScreenTitle(name = stringResource(R.string.order_history))

            FiltersSection(
                chosenOrderTypes = state.chosenOrderTypes,
                chosenDateFilter = state.chosenDateFilterType,
                chosenDateRange = state.chosenDateRange,
                onOrderTypesChange = { onEvent(OrdersHistoryEvent.SetChosenOrderTypes(it)) },
                onDateFilterChange = { onEvent(OrdersHistoryEvent.SetChosenDateFilter(it)) },
                onCustomRangeChange = { onEvent(OrdersHistoryEvent.SetChosenDateRange(it)) },
            )

            val isInitialEmpty = state.fullData.isEmpty()
            val listToShow = if (state.anyFiltersAreApplied) state.filteredData else state.fullData

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.MarginSmall8),
                verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
            ) {
                if (listToShow.isEmpty()) {
                    item {
                        TooltipText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.MarginStandard16),
                            textRes = if (isInitialEmpty) {
                                R.string.order_history_is_empty // нет заказов в истории
                            } else {
                                R.string.order_history_is_empty_by_filters // пусто из-за фильтров
                            },
                            extraTextRes = if (isInitialEmpty) {
                                R.string.order_history_is_empty_extra
                            } else {
                                R.string.order_history_is_empty_by_filters_extra
                            }
                        )
                    }
                } else {
                    itemsIndexed(
                        items = listToShow,
                        key = { _, order -> order.id }
                    ) { _, order ->
                        OrderHistoryCard(
                            modifier = Modifier.animateItem(tween(ANIMATION_DURATION_FAST)),
                            order = order,
                            onClick = { navController.navigateToOrderInfo(order.id) }
                        )
                    }
                }
            }
        }
    }
}
