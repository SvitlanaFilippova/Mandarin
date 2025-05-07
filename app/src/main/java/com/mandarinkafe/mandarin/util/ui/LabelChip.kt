package com.mandarinkafe.mandarin.util.ui

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
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.search.ui.model.LabelUiModel
import com.mandarinkafe.mandarin.util.Constants.MAX_LINES_FOR_LABEL_CHIP

@Composable
fun LabelChip(label: LabelUiModel) {
    Box(
        modifier = Modifier
            .background(label.backgroundColor, shape = RoundedCornerShape(Dimens.CornerRadius8))
            .padding(Dimens.MarginSuperSmall4),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = label.name,
            color = Color.White,
            style = Typography.MealLabelTextStyle,
            textAlign = TextAlign.End,
            maxLines = MAX_LINES_FOR_LABEL_CHIP,
            overflow = TextOverflow.Ellipsis
        )
    }
}