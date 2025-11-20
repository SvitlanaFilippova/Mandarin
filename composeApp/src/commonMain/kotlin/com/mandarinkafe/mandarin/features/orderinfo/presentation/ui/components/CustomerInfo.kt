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
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.filterPaymentInfoForUser
import com.mandarinkafe.mandarin.util.formatPhoneNumberForUi
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CustomerInfo(
    phone: String?,
    comment: String?,
    customerName: String?,
) {
    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Row(
            modifier = Modifier
                .padding(Dimens.MarginStandard16)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(end = Dimens.MarginStandard16),
                painter = painterResource(MR.images.ic_account_circle),
                tint = Colors.WhiteTransparent75,
                contentDescription = null
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)
            ) {
                phone?.let {
                    LabelValue(stringResource(MR.strings.label_phone), it.formatPhoneNumberForUi())
                }
                customerName?.let {
                    LabelValue(stringResource(MR.strings.label_customer), it)
                }
                comment?.let {
                    val visibleComment = it.filterPaymentInfoForUser()
                    if (visibleComment.isNotEmpty()) {
                    Label(stringResource(MR.strings.label_comment))
                    Value(it.filterPaymentInfoForUser())
                }}
            }
        }
    }
}
