package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.navigation.extensions.navigateToOrderInfo
import com.mandarinkafe.mandarin.util.Constants.ANIMATION_DURATION_FAST
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText

@Composable
fun OrdersHistoryList(
    filteredData: List<SavedOrder>,
    fullData: List<SavedOrder>,
    anyFiltersAreApplied: Boolean,
    navController: NavHostController
) {
    val isInitialEmpty = fullData.isEmpty()
    val listToShow = if (anyFiltersAreApplied) filteredData else fullData

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