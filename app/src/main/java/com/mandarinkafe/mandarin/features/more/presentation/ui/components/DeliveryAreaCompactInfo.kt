package com.mandarinkafe.mandarin.features.more.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.components.DeliveryAreaColorIndicator
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.models.UiDeliveryArea

@Composable
fun DeliveryAreaCompactInfo(
    modifier: Modifier = Modifier,
    deliveryArea: UiDeliveryArea
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.White.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .padding(Dimens.MarginSmall8)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                DeliveryAreaColorIndicator(deliveryArea)

                Column(modifier = Modifier.padding(start = Dimens.MarginSmall8)) {
                    Text( // бесплатно от
                        text =
                            stringResource(
                                R.string.delivery_free_from,
                                deliveryArea.freeDeliveryThreshold,
                            ),
                        style = Typography.RegularLightTextStyle,
                        color = Colors.White
                    )
                    Spacer(modifier = Modifier.size(Dimens.MarginSmall8))
                    Text( // цена доставки
                        text =
                            stringResource(
                                R.string.delivery_price,
                                deliveryArea.deliveryPrice
                            ),
                        style = Typography.RegularLightTextStyle,
                        color = Colors.White
                    )
                }
            }
        }
    }
}