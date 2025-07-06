package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.search.presentation.model.LabelUiModel
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_LABEL_CHIP

@Composable
fun LabelChip(label: LabelUiModel, cardIsSmall: Boolean) {
    val cornerRadius = if (cardIsSmall) Dimens.CornerRadius4 else Dimens.CornerRadius8
    Box(
        modifier = Modifier
            .background(
                label.backgroundColor,
                shape = RoundedCornerShape(
                    topStart = cornerRadius,
                    bottomStart = cornerRadius,
                    topEnd = Dimens.ZeroDp0,
                    bottomEnd = Dimens.ZeroDp0
                )
            )
            .padding(if (cardIsSmall) Dimens.MarginSuperSmall2 else Dimens.MarginSuperSmall4),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = label.name,
            color = Color.White,
            style = if (cardIsSmall) Typography.MealLabelSmallTextStyle else Typography.MealLabelTextStyle,
            textAlign = TextAlign.End,
            maxLines = MAX_LINES_FOR_LABEL_CHIP,
            overflow = TextOverflow.Ellipsis
        )
    }
}