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
import com.mandarinkafe.mandarin.util.ui.components.UndoIndicator

@Composable
fun CartClearTextButton(
    onClear: () -> Unit,
    onCancelClear: () -> Unit,
    isPendingClear: Boolean,
    clearingProgress: Float?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.MarginSuperSmall4, horizontal = Dimens.MarginSuperSmall4),
        contentAlignment = Alignment.CenterEnd
    ) {
        if (isPendingClear && clearingProgress != null) {
            UndoIndicator(
                modifier = Modifier.padding(horizontal = Dimens.MarginStandard16),
                progress = clearingProgress,
                onCancel = onCancelClear
            )
        } else {
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
}