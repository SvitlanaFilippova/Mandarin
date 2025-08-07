package com.mandarinkafe.mandarin.features.orderinfo.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun OrderNeedConfirmSection() {
    Card(colors = CardDefaults.cardColors(containerColor = Colors.DarkGrey)) {
        Row(
            modifier = Modifier
                .padding(Dimens.MarginSmall8)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(end = Dimens.MarginSmall8),
                imageVector = Icons.Default.DateRange,
                tint = Colors.WhiteTransparent75,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.order_need_to_be_confirmed),
                style = Typography.RegularTextStyle
            )
        }
    }
}
