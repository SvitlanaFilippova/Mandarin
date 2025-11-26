package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.presentation.models.toUi
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.toUi
import com.mandarinkafe.mandarin.features.ordershistory.domain.models.SavedOrder
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ActiveOrderCard(
    modifier: Modifier = Modifier,
    order: SavedOrder,
    onClick: () -> Unit,
) {
    val orderStatus = order.status

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.MarginSmall8)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey),
        shape = RoundedCornerShape(Dimens.CornerRadius8)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = Dimens.MarginStandard16,
                vertical = Dimens.MarginSmall8
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            order.orderType?.let {
                val text = if (order.number.isNotEmpty()) {
                    stringResource(it.toUi().nameRes) + " • №${order.number}"
                } else {
                    stringResource(it.toUi().nameRes)
                }
                Text(
                    text = text,
                    style = Typography.RegularTextStyle,
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(
                visible = orderStatus != null,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut() + scaleOut(targetScale = 0.8f)
            ) {
                val color = when (orderStatus) {
                    DeliveryStatus.UNCONFIRMED -> Colors.Orange
                    else -> Colors.Green
                }
                val orderUiStatus = remember(orderStatus) { orderStatus?.toUi() }

                orderUiStatus?.let {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(orderUiStatus.iconRes),
                            contentDescription = stringResource(orderUiStatus.nameRes),
                            tint = color
                        )
                        Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
                        Text(
                            text = stringResource(orderUiStatus.nameRes),
                            style = Typography.RegularTextStyle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = color
                        )
                    }
                }
            }
        }
    }
}
