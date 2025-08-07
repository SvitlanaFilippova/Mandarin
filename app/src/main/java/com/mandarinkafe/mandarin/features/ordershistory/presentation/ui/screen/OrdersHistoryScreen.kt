package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components.OrderHistoryCard
import com.mandarinkafe.mandarin.features.ordershistory.presentation.viewmodel.OrdersHistoryViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToOrderInfo

@Composable
fun OrdersHistoryScreen(
    navController: NavHostController,
    viewModel: OrdersHistoryViewModel = hiltViewModel()
) {
    val orders = listOf(
        SavedOrder(
            id = "5b0c689f-9f2c-484e-9277-202fe5c97188",
            whenCreated = "15:30 04.08.2025",
            orderType = "Доставка курьером",
            address = "Ул. Солнечная, 4, кв. 82, Черноголовка"
        ),
        SavedOrder(
            id = "6d7b09bd-b47c-469b-a212-177f77899f37",
            whenCreated = "17:30 06.08.2025",
            orderType = "Cамовывоз",
        ),
        SavedOrder(
            id = "eac2bbc0-e504-49c7-aeb1-44aad1a62497",
            whenCreated = "17:50 06.08.2025",
            orderType = "Cамовывоз"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.MarginSmall8),
        verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)

    ) {
        item {
            Text(
                modifier = Modifier.padding(Dimens.MarginSmall8),
                text = stringResource(R.string.order_history),
                style = Typography.TitleStyle,
            )
        }

        itemsIndexed(
            items = orders,
            key = { _, order -> order.id }
        ) { index, order ->
            OrderHistoryCard(
                order = order,
                onClick = {
                    navController.navigateToOrderInfo(order.id)
                })
        }
    }
}
