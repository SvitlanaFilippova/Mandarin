package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_LABEL_CHIP

@Composable
fun NoDeliveryChip(modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(bottom = Dimens.MarginSmall8),
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Colors.LabelDefault,
                    shape = RoundedCornerShape(
                        topStart = Dimens.CornerRadius8,
                        bottomStart = Dimens.CornerRadius8,
                        topEnd = Dimens.ZeroDp0,
                        bottomEnd = Dimens.ZeroDp0
                    )
                )
                .padding(Dimens.MarginSuperSmall4),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = stringResource(R.string.for_selfpickup),
                color = Color.White,
                style = Typography.MealLabelSmallTextStyle,
                textAlign = TextAlign.End,
                maxLines = MAX_LINES_FOR_LABEL_CHIP,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}