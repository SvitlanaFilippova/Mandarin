package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun OrderStatusSection(order: IncomingOrder) {
    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Column(
            Modifier.padding(Dimens.MarginStandard16),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
        ) {
            order.errorInfo?.let { LabelValue("Ошибка", it.message.toString()) }
            LabelValue("Статус заказа", stringResource(order.status.labelResId))
            order.orderType?.let { LabelValue("Cпособ получения", it.name) }
            order.paymentName?.let { LabelValue("Способ оплаты", it) }
        }
    }
}