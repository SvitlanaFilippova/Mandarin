package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun OrderTimesSection(order: IncomingOrder) {
    val hasTimelineData = with(order) {
        whenCreated != null ||
                whenConfirmed != null ||
                whenCancelled != null ||
                whenCookingCompleted != null ||
                whenSent != null ||
                whenDelivered != null ||
                whenClosed != null
    }
    if (!hasTimelineData) return

    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Row(
            modifier = Modifier
                .padding(Dimens.MarginStandard16)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(end = Dimens.MarginStandard16),
                painter = painterResource(MR.images.ic_clock),
                tint = Colors.WhiteTransparent75,
                contentDescription = null
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
            ) {
                with(order) {
                    whenCreated?.let {
                        LabelValue(
                            stringResource(MR.strings.label_created_when),
                            it
                        )
                    }
                    whenConfirmed?.let {
                        LabelValue(
                            stringResource(MR.strings.label_confirmed_when),
                            it
                        )
                    }
                    whenCancelled?.let {
                        LabelValue(
                            stringResource(MR.strings.label_cancelled_when),
                            it
                        )
                    }
                    whenCookingCompleted?.let {
                        LabelValue(
                            stringResource(MR.strings.label_cooking_completed_when),
                            it
                        )
                    }
                    whenSent?.let { LabelValue(stringResource(MR.strings.label_sent_when), it) }
                    whenDelivered?.let {
                        LabelValue(
                            stringResource(MR.strings.label_delivered_when),
                            it
                        )
                    }
                    whenClosed?.let { LabelValue(stringResource(MR.strings.label_closed_when), it) }
                }
            }
        }
    }
}
