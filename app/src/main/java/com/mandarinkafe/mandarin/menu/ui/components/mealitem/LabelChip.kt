package com.mandarinkafe.mandarin.menu.ui.components.mealitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography
import com.mandarinkafe.mandarin.util.Constants.NEW_LABEL
import com.mandarinkafe.mandarin.util.Constants.VEG_LABEL

@Composable
fun LabelChip(text: String, backgroundColor: Color? = null) {

    val finalBackgroundColor = when (text.lowercase()) {
        VEG_LABEL.lowercase() -> Colors.LabelVegGreen
        NEW_LABEL.lowercase() -> Colors.LabelNewRed
        else -> backgroundColor ?: Colors.LabelDefault
    }

    Box(
        modifier = Modifier
            .background(finalBackgroundColor, shape = RoundedCornerShape(Dimens.CornerRadius8))
            .padding(horizontal = Dimens.MarginSmall8, vertical = Dimens.MarginSuperSmall4)
    ) {
        Text(
            text = text,
            color = Color.White,
            style = Typography.MealLalesTextStyle
        )
    }
}