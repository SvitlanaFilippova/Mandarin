package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun UndoIndicator(
    modifier: Modifier = Modifier,
    progress: Float,
    onCancel: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(Dimens.ButtonToCartSmall36)
            .widthIn(min = Dimens.ButtonToCartBig120)
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(Colors.DarkGrey)
    ) {
        Row(
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = onCancel),
            horizontalArrangement = Arrangement.spacedBy(
                Dimens.MarginSmall8,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                progress = { progress },
                color = Colors.White,
                strokeWidth = Dimens.Elevation4,
                trackColor = Colors.Transparent,
                modifier = Modifier.size(Dimens.ProgressBarSmallSize)
            )

            Text(
                text = stringResource(MR.strings.cancel_removing),
                style = Typography.ToCartButtonStyle
            )
        }
    }
}
