package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun OrderSummaryData(
    cartSum: Int,
    discountSum: Float,
    discountPercent: Int,
    deliveryCost: Int,
    addressValidated: Boolean?,
    freeDeliveryThreshold: Int?,
    addressValidationInProgress: Boolean,
) {
    val freeDeliveryThreshold = freeDeliveryThreshold ?: 0

    Column {
        Spacer(modifier = Modifier.size(Dimens.MarginStandard16))
        OrderSummaryRow(
            name = stringResource(R.string.total_cart_cost),
            amount = cartSum.toFloat()
        )

        // Информацию о скидке показываем только если по номеру телефона найдена скидочная карта
        if (discountPercent > 0) {
            OrderSummaryRow(
                name = stringResource(R.string.discount_template, discountPercent),
                amount = discountSum,
                hintText = stringResource(R.string.discount_hint)
            )
        }



        when (addressValidated) {
            true -> {
                OrderSummaryRow(
                    name = stringResource(R.string.delivery_cost),
                    amount = deliveryCost.toFloat(),
                    hintText = stringResource(
                        R.string.delivery_cost_hint, freeDeliveryThreshold
                    )
                )
            }

            false -> {
                Text(
                    text = stringResource(R.string.delivery_validation_error),
                    style = Typography.RegularLightTextStyle
                )
            }

            null -> {
                OrderSummaryRow(
                    name = stringResource(R.string.delivery_cost),
                    inProgress = addressValidationInProgress,
                    amount = null,
                )
            }
        }

        Spacer(modifier = Modifier.size(Dimens.MarginStandard16))
    }
}

