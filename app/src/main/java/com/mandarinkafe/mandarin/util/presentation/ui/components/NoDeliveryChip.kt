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
import com.mandarinkafe.mandarin.util.LabelSize

@Composable
fun NoDeliveryChip(modifier: Modifier, size: LabelSize) {
    val cornerRadius = when (size) {
        LabelSize.SMALL -> Dimens.CornerRadius4
        LabelSize.MEDIUM -> Dimens.CornerRadius8
        LabelSize.BIG -> Dimens.CornerRadius8
    }

    val padding = when (size) {
        LabelSize.SMALL -> Dimens.MarginSuperSmall2
        LabelSize.MEDIUM -> Dimens.MarginSuperSmall4
        LabelSize.BIG -> Dimens.MarginSmall8
    }
    val style = when (size) {
        LabelSize.SMALL -> {
            Typography.MealLabelSmallTextStyle
        }

        LabelSize.MEDIUM -> {
            Typography.MealLabelTextStyle
        }

        LabelSize.BIG -> {
            Typography.MealLabelBigTextStyle
        }
    }
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Colors.Brown.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(
                        topStart = cornerRadius,
                        bottomStart = cornerRadius,
                    )
                )
                .padding(padding),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = stringResource(R.string.label_selfpickup_only),
                color = Color.White,
                style = style,
                textAlign = TextAlign.End,
                maxLines = MAX_LINES_FOR_LABEL_CHIP,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}