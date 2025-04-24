package com.mandarinkafe.mandarin.cart.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography

@Composable
fun CarClearRow(onClear: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.MarginSuperSmall4, horizontal = Dimens.MarginSuperSmall4),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClear)
                .padding(Dimens.MarginSuperSmall4),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                modifier = Modifier.padding(horizontal = Dimens.MarginSmall8),
                text = stringResource(R.string.clear_cart),
                style = Typography.SmallTextStyle,
                color = Colors.Grey

            )

            Icon(
                modifier = Modifier
                    .size(Dimens.IconSize24),
                imageVector = Icons.Default.Delete,
                tint = Colors.Grey,
                contentDescription = stringResource(R.string.clear_cart),

                )
        }
    }
}