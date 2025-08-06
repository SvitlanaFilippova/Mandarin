package com.mandarinkafe.mandarin.features.orderconfirmation.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.domain.models.IncomingOrder
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun OrderTimesSection(order: IncomingOrder) {
    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Row(
            modifier = Modifier
                .padding(Dimens.MarginStandard16)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(end = Dimens.MarginStandard16),
                imageVector = Icons.Default.DateRange,
                tint = Colors.WhiteTransparent75,
                contentDescription = null
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
            ) {
                with(order) {
                    whenCreated?.let { LabelValue("Создан", it) }
                    whenConfirmed?.let { LabelValue("Подтверждён", it) }
                    whenCancelled?.let { LabelValue("Отменён", it) }
                    whenCookingCompleted?.let { LabelValue("Готово", it) }
                    whenPacked?.let { LabelValue("Упаковано", it) }
                    whenSent?.let { LabelValue("Отправлено", it) }
                    whenDelivered?.let { LabelValue("Доставлено", it) }
                    whenClosed?.let { LabelValue("Закрыто", it) }
                }
            }
        }
    }
}
