package com.mandarinkafe.mandarin.util.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography

@Composable
fun UndoIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    onCancel: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(Dimens.ButtonToCartSmall32)
            .widthIn(min = Dimens.ButtonToCartBig120)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.DarkGrey)
    ) {
        Row(
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = onCancel),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                progress = { progress },
                color = Colors.White,
                strokeWidth = Dimens.Elevation2,
                trackColor = Colors.Transparent,
                modifier = Modifier
                    .size(Dimens.ButtonToCartSmall32)
                    .padding(Dimens.MarginSmall8)
            )

            Text(
                text = stringResource(R.string.cancel_removing),
                style = Typography.ToCartButtonStyle
            )
        }
    }
}