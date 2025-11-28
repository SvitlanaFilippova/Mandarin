package com.mandarinkafe.mandarin.util.presentation.ui.components.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.address.presentation.ui.models.UiDeliveryArea
import com.mandarinkafe.mandarin.util.presentation.ui.components.DeliveryAreaColorIndicator
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun DeliveryAreaInfo(
    modifier: Modifier = Modifier,
    deliveryArea: UiDeliveryArea?,
) {
    Box(
        modifier = modifier
            .padding(Dimens.MarginSmall8)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.AppBlack80)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.MarginSmall8),
        ) {
            if (deliveryArea != null) {
                DeliveryAreaColorIndicator(deliveryArea)

                Text( // Мин сумма заказа и цена доставки
                    modifier = Modifier.padding(start = Dimens.MarginSmall8),
                    text =
                        stringResource(
                            MR.strings.delivery_price_and_free_from,
                            deliveryArea.freeDeliveryThreshold,
                            deliveryArea.deliveryPrice
                        ),
                    style = Typography.RegularLightTextStyle,
                    color = Colors.White
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(MR.images.ic_info),
                        modifier = Modifier
                            .padding(Dimens.MarginSmall8),
                        tint = Colors.White.copy(alpha = 0.5f),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = Dimens.MarginSmall8),
                        text = stringResource(
                            MR.strings.delivery_validation_error
                        ),
                        style = Typography.RegularLightTextStyle,
                        color = Colors.White
                    )
                }
            }
        }
    }
}