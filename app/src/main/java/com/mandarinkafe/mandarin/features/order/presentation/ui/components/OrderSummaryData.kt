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
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType

@Composable
fun OrderSummaryData(
    cartSum: Int,
    discountSum: Float,
    discountPercent: Int,
    deliveryType: DeliveryType?,
    deliveryCost: Int,
    addressInNotInDeliveryArea: Boolean,
    freeDeliveryThreshold: Int?,
) {
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

// Стоимость доставки показываем только если НЕ выбран самовывоз
        if (deliveryType != DeliveryType.SELF_PICKUP) {
            when (addressInNotInDeliveryArea) {
                true -> {
                    Text(
                        text = stringResource(R.string.delivery_validation_error),
                        style = Typography.RegularLightTextStyle
                    )
                }

                false -> {
                    val hintText = if (freeDeliveryThreshold != null) {
                        stringResource(
                            R.string.delivery_cost_hint, freeDeliveryThreshold
                        )
                    } else {
                        null
                    }

                    OrderSummaryRow(
                        name = stringResource(R.string.delivery_cost),
                        amount = deliveryCost.toFloat(),
                        hintText = hintText
                    )
                }
            }
        }
        Spacer(modifier = Modifier.size(Dimens.MarginStandard16))
    }
}

