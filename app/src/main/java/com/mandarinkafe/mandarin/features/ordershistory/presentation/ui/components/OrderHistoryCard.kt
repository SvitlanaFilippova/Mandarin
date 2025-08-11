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
import androidx.compose.ui.tooling.preview.Preview
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder

@Preview
@Composable
fun OrderHistoryCardPreview() {
    OrderHistoryCard(
        SavedOrder(
            id = "Tw46twtwasfgvesdzfxcasdfcadf",
            timestamp = 24242424,
            whenCreated = "15:33, 09.08.25",
            orderType = "Доставка курьером",
            addressLine1 = "Ул. Солнечная, 4, Черноголовка",
            addressDetails = "кв. 82, п.2, этаж 10",
            mealNames = "Пицца Маргарита x1, Ролл Филадельфия x2, Морс клюквенный 1x, Васаби, Пиво x199"
        ),
        onClick = { }
    )
}

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

            Spacer(modifier = Modifier.height(Dimens.MarginSmall8))

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
                    fontWeight = FontWeight.ExtraLight,
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
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(Dimens.MarginStandard16))

            // ID заказа
            Text(
                text = "ID: ${order.id}",
                style = Typography.ExtraSmallTextStyle,
                color = Colors.LightGrey
            )
        }
    }
}