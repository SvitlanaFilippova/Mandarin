package com.mandarinkafe.mandarin.features.address.address.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.models.UiDeliveryArea

@Composable
fun DeliveryAreaInfo(
    modifier: Modifier = Modifier,
    deliveryArea: UiDeliveryArea?
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.MarginSmall8)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.WhiteTransparent75)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.MarginSmall8),
        ) {
            if (deliveryArea != null) {
                // Цвет и номер зоны доставки
                Box(
                    modifier = Modifier
                        .size(Dimens.IconSize24)
                        .clip(RoundedCornerShape(Dimens.CornerRadius8))
                        .background(deliveryArea.color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = deliveryArea.id.toString(),
                        style = Typography.RegularLightTextStyle,
                        color = Colors.White
                    )
                }

                Text( // Мин сумма заказа и цена доставки
                    modifier = Modifier.padding(start = Dimens.MarginSmall8),
                    text =
                        stringResource(
                            R.string.free_delivery_at,
                            deliveryArea.freeDeliveryThreshold,
                            deliveryArea.deliveryPrice
                        ),
                    style = Typography.RegularLightTextStyle,
                    color = Colors.AppBlack
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(Dimens.MarginSmall8),
                        imageVector = Icons.Default.Info,
                        tint = Colors.AppBlack.copy(alpha = 0.5f),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = Dimens.MarginSmall8),
                        text = stringResource(
                            R.string.delivery_validation_error
                        ),
                        style = Typography.RegularLightTextStyle,
                        color = Colors.AppBlack
                    )
                }
            }
        }
    }
}