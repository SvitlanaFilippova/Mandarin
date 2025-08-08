package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText

@Composable
fun OrderSummaryData(
    cartSum: Int,
    discountSize: Int,
    discountSum: Double,
    isPickup: Boolean,
    deliveryCost: Int,
    addressInNotInDeliveryArea: Boolean,
    freeDeliveryThreshold: Int?,
    containNotDiscountable: Boolean,
) {
    Column(modifier = Modifier.padding(Dimens.MarginSmall8)) {
        Spacer(modifier = Modifier.size(Dimens.MarginStandard16))
        OrderSummaryRow(
            name = stringResource(R.string.total_cart_cost),
            amount = cartSum.toFloat()
        )

        // Информацию о скидке показываем только если есть скидочная карта
        if (discountSize > 0) {
            val hintText =
                if (containNotDiscountable) stringResource(R.string.discount_hint) else null
            OrderSummaryRow(
                name = stringResource(R.string.discount_template, discountSize),
                amount = -discountSum.toFloat(),
                hintText = hintText
            )
        }

        // Стоимость доставки показываем только если НЕ выбран самовывоз
        if (!isPickup) {
            when (addressInNotInDeliveryArea) {
                true -> {
                    TooltipText(textRes = R.string.delivery_validation_error)
                }

                false -> {
                    val hintText = if (freeDeliveryThreshold != null) {
                        stringResource(
                            R.string.delivery_cost_hint,
                            freeDeliveryThreshold
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

