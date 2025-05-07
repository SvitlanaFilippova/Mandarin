package com.mandarinkafe.mandarin.menu.ui.components.mealitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.search.ui.model.LabelUiModel

@Composable
fun LabelChip(label: LabelUiModel) {
    Box(
        modifier = Modifier
            .background(label.backgroundColor, shape = RoundedCornerShape(Dimens.CornerRadius8))
            .padding(horizontal = Dimens.MarginSmall8, vertical = Dimens.MarginSuperSmall4)
    ) {
        Text(
            text = label.name,
            color = Color.White,
            style = Typography.MealLabelTextStyle
        )
    }
}