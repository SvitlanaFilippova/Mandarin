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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.presentation.models.toUi
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun OrderHistoryCard(modifier: Modifier = Modifier, order: SavedOrder, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey),
    ) {
        Column(modifier = Modifier.padding(Dimens.MarginStandard16)) {
            DateAndStatusSection(
                orderStatus = order.status,
                whenCreated = order.whenCreated
            )

            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

            // Тип и номер заказа
            order.orderType?.let {
                val text = if (order.number.isNotEmpty()) {
                    stringResource(it.toUi().nameRes) + " • №${order.number}"
                } else {
                    stringResource(it.toUi().nameRes)
                }
                Text(
                    text = text,
                    style = Typography.RegularTextStyle,
                )
            }

            // Адрес
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
                    fontWeight = FontWeight.Light,
                )
            }
            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

            // Блюда в заказе строкой
            if (order.mealNames.isNotEmpty()) {
                Text(
                    text = order.mealNames,
                    style = Typography.SmallTextStyle,
                    color = Colors.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
                )
            }

            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

        }
    }
}