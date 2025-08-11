package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components.OrderHistoryCard
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToOrderInfo
import com.mandarinkafe.mandarin.util.presentation.ui.components.ScreenTitle
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText

@Composable
fun OrdersHistoryScreen(
    navController: NavHostController,
    viewModel: OrdersHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val orders = state.data

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)

    ) {

        item {
            ScreenTitle(name = stringResource(R.string.order_history))
        }

        if (orders.isNotEmpty()) {
            itemsIndexed(
                items = orders,
                key = { _, order -> order.id }
            ) { index, order ->
                OrderHistoryCard(
                    order = order,
                    onClick = {
                        navController.navigateToOrderInfo(order.id)
                    }
                )
            }
        } else {
            item {
                TooltipText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.MarginStandard16),
                    textRes = R.string.order_history_is_empty,
                    extraTextRes = R.string.order_history_is_empty_extra
                )
            }
        }
    }
}
