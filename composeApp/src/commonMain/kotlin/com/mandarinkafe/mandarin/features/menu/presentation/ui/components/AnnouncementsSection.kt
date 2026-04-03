package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.menu.presentation.models.OrderClosingBannerUi
import com.mandarinkafe.mandarin.util.presentation.ui.components.OrderAcceptStatusBanner
import com.mandarinkafe.mandarin.util.presentation.ui.components.TooltipText
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AnnouncementsSection(
    announcements: List<String>,
    orderClosingBanner: OrderClosingBannerUi? = null,
) {
    OrderAcceptStatusSection(orderClosingBanner)

    announcements.forEach { announcement ->
        if (announcement.isNotEmpty()) {
            TooltipText(
                modifier = Modifier.padding(Dimens.MarginSmall8),
                text = announcement
            )
        }
    }
}

@Composable
private fun OrderAcceptStatusSection(orderClosingBanner: OrderClosingBannerUi?) {
    when (orderClosingBanner) {
        is OrderClosingBannerUi.WithClosingTime -> {
            OrderAcceptStatusBanner(
                modifier = Modifier.padding(Dimens.MarginSmall8),
                text = stringResource(MR.strings.error_we_are_closing, orderClosingBanner.hhMm),
                tint = Colors.Red,
            )
        }

        is OrderClosingBannerUi.ClosedToday -> {
            OrderAcceptStatusBanner(
                modifier = Modifier.padding(Dimens.MarginSmall8),
                text = stringResource(MR.strings.cafe_closed_today_announcement),
                tint = Colors.Red,
            )
        }

        is OrderClosingBannerUi.AcceptanceEndingSoon -> {
            OrderAcceptStatusBanner(
                modifier = Modifier.padding(Dimens.MarginSmall8),
                text = stringResource(
                    MR.strings.order_accept_ending_soon_announcement,
                    orderClosingBanner.orderAcceptanceEndTime,
                ),
                tint = Colors.Orange,
            )
        }

        null -> Unit
    }
}
