package com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.viewmodel.OrderConfirmationContract.OrderConfirmationEvent
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.viewmodel.OrderConfirmationContract.OrderConfirmationEvent.StopObservingStatus
import com.mandarinkafe.mandarin.features.orderconfirmation.presentation.viewmodel.OrderConfirmationViewModel
import com.mandarinkafe.mandarin.navigation.extensions.navigateToMenu
import com.mandarinkafe.mandarin.util.presentation.ui.components.buttons.ButtonWithText

@Composable
fun OrderConfirmationScreen(
    orderID: String?,
    viewModel: OrderConfirmationViewModel = hiltViewModel(),
    navController: NavHostController
) {
    if (orderID == null) return
    val onEvent = viewModel::onEvent
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        onEvent(OrderConfirmationEvent.SetInitId(orderID))
    }
    val order = state.orderInfo
    order?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.MarginStandard16),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            Text("ID: ${order.id}")
            order.number?.let {
                Text("Номер: $it")
            }
            Text("Статус создания: ${order.creationStatus}")
            order.errorInfo?.let {
                Text("Ошибка: ${it.message}")
            }
            Text("Кол-во позиций: ${order.items.count()}")
            Spacer(modifier = Modifier.size(Dimens.MarginStandard16))
            order.orderType?.let {
                Text("Тип заказа: ${it.name}")
            }
            order.courierInfo?.let {
                Text("Курьер:")
                Text("${it.courier.name}, ${it.courier.phone}")
            }
            Spacer(modifier = Modifier.size(Dimens.MarginStandard16))

            order.payments?.let { Text("Способы оплаты: $it") }
            order.status?.let { Text("Статус заказа: $it") }
            order.cancelInfo?.let { Text("Причина отмены: $it") }

            order.sum?.let { Text("Сумма заказа: $it") }
            order.processedPaymentsSum?.let { Text("Оплачено: $it") }
            order.whenCreated?.let { Text("Создан: $it") }
            order.whenConfirmed?.let { Text("Подтверждён: $it") }
            order.whenCookingCompleted?.let { Text("Готово: $it") }
            order.whenPacked?.let { Text("Упаковано: $it") }
            order.whenSended?.let { Text("Отправлено: $it") }
            order.whenDelivered?.let { Text("Доставлено: $it") }
            order.whenPrinted?.let { Text("Напечатано: $it") }
            order.whenClosed?.let { Text("Закрыто: $it") }


            Spacer(modifier = Modifier.size(Dimens.MarginStandard16))
            order.phone?.let { Text("Телефон: $it") }
            order.comment?.let { Text("Комментарий: $it") }
            order.customer?.let { Text("Клиент: ${it.name}") }

            ButtonWithText(
                textResID = R.string.back_to_menu,
                onClick = { navController.navigateToMenu() }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose { onEvent(StopObservingStatus) }
    }
}

