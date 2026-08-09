package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.orderinfo.domain.models.DeliveryStatus
import com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.models.toUi
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun HistoryOrderStatusSection(
    orderStatus: DeliveryStatus?,
    isCreationError: Boolean = false,
    color: Color,
) {
    val orderUiStatus = remember(orderStatus) { orderStatus?.toUi() }
    val iconRes = if (isCreationError) MR.images.ic_error else orderUiStatus?.iconRes
    val title = when {
        isCreationError -> stringResource(MR.strings.order_creation_status_error)
        orderUiStatus != null -> stringResource(orderUiStatus.nameRes)
        else -> null
    }

    if (iconRes != null && title != null) {
        Row(verticalAlignment = Alignment.Bottom) {
            Icon(
                painterResource(iconRes),
                contentDescription = title,
                tint = color
            )
            Spacer(modifier = Modifier.width(Dimens.MarginSmall8))
            Text(
                text = title,
                style = Typography.RegularTextStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = color
            )
        }
    }
}
