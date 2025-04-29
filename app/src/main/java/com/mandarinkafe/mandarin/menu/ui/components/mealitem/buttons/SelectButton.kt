package com.mandarinkafe.mandarin.menu.ui.components.mealitem.buttons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography

@Composable
fun SelectButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        contentPadding = PaddingValues(Dimens.MarginSuperSmall4),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.Orange,
            contentColor = Color.White
        ),
        modifier = modifier
            .widthIn(min = Dimens.ButtonToCartBig120)
            .height(Dimens.ButtonToCartSmall32)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = text,
                tint = Color.White
            )
            Text(
                text = text,
                style = Typography.ToCartButtonStyle
            )
        }
    }
}