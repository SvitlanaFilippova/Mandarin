package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

@Composable
fun OrderHistoryCard(order: SavedOrder, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey),
    ) {
        Column(modifier = Modifier.padding(Dimens.MarginStandard16)) {
            // Дата + тип заказа
            Text(
                text = "${order.whenCreated} • ${order.orderType}",
                style = Typography.RegularTextStyle,
            )

            Spacer(modifier = Modifier.height(Dimens.MarginSuperSmall4))

            // Адрес (если есть)
            if (order.addressLine1.isNotEmpty()) {
                Text(
                    text = order.addressLine1,
                    style = Typography.RegularLightTextStyle,
                )
            }
            if (order.addressDetails.isNotEmpty()) {
                Text(
                    text = order.addressDetails,
                    style = Typography.SmallTextStyle,
                )
            }

            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))

            // ID заказа (очень мелкий шрифт)
            Text(
                text = "ID: ${order.id}",
                style = Typography.ExtraSmallTextStyle,
                color = Colors.LightGrey
            )
        }
    }
}