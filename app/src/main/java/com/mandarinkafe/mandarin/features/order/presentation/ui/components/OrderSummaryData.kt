package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun OrderSummaryData(
    cartSum: Int,
    discountSum: Int,
    discountPercent: Int,
    deliveryCost: Int,
) {
    Column {
        Spacer(modifier = Modifier.size(Dimens.MarginStandard16))
        OrderSummaryRow(
            name = stringResource(R.string.total_cart_cost),
            amount = cartSum
        )

        // Информацию о скидке показываем только если по номеру телефона найдена скидочная карта
        if (discountPercent > 0) {
            OrderSummaryRow(
                name = stringResource(R.string.discount_template, discountPercent),
                amount = discountSum,
                hintResId = R.string.discount_hint
            )
        }

        OrderSummaryRow(
            name = stringResource(R.string.delivery_cost),
            amount = deliveryCost,
            hintResId = R.string.delivery_cost_hint
        )

        Spacer(modifier = Modifier.size(Dimens.MarginStandard16))
    }
}

