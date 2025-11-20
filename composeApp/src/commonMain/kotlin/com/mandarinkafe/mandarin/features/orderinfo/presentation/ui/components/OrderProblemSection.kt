package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.order.domain.models.ErrorInfo
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun OrderProblemSection(errorInfo: ErrorInfo?) {
    errorInfo?.let {
        Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
            Column(
                Modifier.padding(Dimens.MarginStandard16),
                verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
            ) {

                LabelValue(stringResource(MR.strings.label_error), it.message.toString())
            }
        }
    }
}

