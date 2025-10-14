package com.mandarinkafe.mandarin.features.address.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.address.address.presentation.ui.models.UiDeliveryArea

@Composable
fun DeliveryAreaColorIndicator(deliveryArea: UiDeliveryArea) {
    Box(
        modifier = Modifier
            .size(Dimens.IconSize24)
            .background(
                color = deliveryArea.color.copy(alpha = 0.4f),
                shape = RoundedCornerShape(Dimens.CornerRadius8)
            )
            .border(
                width = Dimens.Border1,
                color = deliveryArea.color.copy(alpha = 0.6f),
                shape = RoundedCornerShape(Dimens.CornerRadius8)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = deliveryArea.id.toString(),
            style = Typography.RegularLightTextStyle,
            color = Colors.White
        )
    }
}