package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.domain.models.OrderPickupPoint
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun SelfPickupInfo(
    visible: Boolean,
    pickupPoint: OrderPickupPoint,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Box(
            modifier = modifier.border(
                BorderStroke(width = Dimens.Border1, color = Colors.DarkGrey),
                shape = RoundedCornerShape(Dimens.CornerRadius8)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(
                        start = Dimens.MarginStandard16,
                        top = Dimens.MarginStandard16,
                        bottom = Dimens.MarginStandard16
                    ),
                    painter = painterResource(MR.images.ic_location_on),
                    tint = Colors.WhiteTransparent75,
                    contentDescription = null
                )
                Column(
                    modifier = Modifier.padding(Dimens.MarginStandard16),
                    verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
                ) {
                    val content = when (pickupPoint) {
                        OrderPickupPoint.PIZZERIA -> listOf(
                            MR.strings.pickup_pizzeria_address to Typography.SmallTextStyle
                        )

                        OrderPickupPoint.CAFE -> listOf(
                            MR.strings.pickup_cafe_address to Typography.SmallTextStyle
                        )

                        OrderPickupPoint.BOTH -> listOf(
                            MR.strings.pickup_both_title to Typography.RegularTextStyle,
                            MR.strings.pickup_both_description to Typography.SmallTextStyle,
                            MR.strings.pickup_pizzeria_address to Typography.SmallTextStyle,
                            MR.strings.pickup_cafe_address to Typography.SmallTextStyle
                        )
                    }

                    content.forEach { (textRes, style) ->
                        Text(
                            text = stringResource(textRes),
                            style = style.copy(color = Colors.WhiteTransparent75)
                        )
                    }
                }
            }
        }
    }
}