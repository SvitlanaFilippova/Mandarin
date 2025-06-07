package com.mandarinkafe.mandarin.shared.cart.ui.components

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
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun CartClearTextButton(
    onClear: () -> Unit,
) {
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
                color = Colors.LightGrey

            )

            Icon(
                modifier = Modifier
                    .size(Dimens.IconSize24),
                imageVector = Icons.Default.Delete,
                tint = Colors.LightGrey,
                contentDescription = stringResource(R.string.clear_cart),

                )
        }

    }
}