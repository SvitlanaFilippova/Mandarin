package com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.orderconfirmation.domain.models.IncomingOrderItem

@Composable
fun OrderItemsSection(items: List<IncomingOrderItem>, sum: Double?) {
    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Column(Modifier.padding(Dimens.MarginStandard16)) {
            Label("Позиции заказа: ${items.count()}")
            Spacer(Modifier.height(Dimens.MarginSmall8))
            items.forEach {
                SmallHorizontalMealItemCard(
                    item = it,
                    onMealDetailsClick = {}
                )
                HorizontalDivider(
                    Modifier.height(Dimens.DividerHeight1),
                )
                Spacer(Modifier.height(Dimens.MarginSmall8))
            }

            sum?.let {
                LabelValue(
                    "Итого",
                    stringResource(R.string.float_price_template, it.toFloat())
                )
            }
        }
    }
}