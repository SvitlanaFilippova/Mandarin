package com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun OrderInfoSection(order: IncomingOrder) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(Dimens.MarginSmall8),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        order.number?.let {
            Text(
                text = "Заказ №$it, создан ${order.whenCreated}",
                style = Typography.RegularLightTextStyle
            )
        }
    }
}
