package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.IncomingOrderItem

@Composable
fun OrderItemsSection(items: List<IncomingOrderItem>, sum: Double?) {
    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Column(
            modifier = Modifier
                .padding(Dimens.MarginStandard16)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
        ) {
            Label(stringResource(R.string.label_order_items, items.count()))
            Spacer(Modifier.height(Dimens.MarginSmall8))
            items.forEach {
                OrderMealItemCard(
                    item = it,
                    onMealDetailsClick = {}
                )
                HorizontalDivider(
                    Modifier.height(Dimens.DividerHeight1),
                )
                Spacer(Modifier.height(Dimens.MarginSmall8))
            }

            sum?.let {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.label_total),
                        style = Typography.RegularTextStyle
                    )
                    Text(
                        text = stringResource(
                            R.string.float_price_template,
                            it.toFloat()
                        ),
                        style = Typography.RegularTextStyle
                    )
                }
            }

        }
    }
}
