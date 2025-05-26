package com.mandarinkafe.mandarin.util.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography

@Composable
fun CustomizeButtonWithText(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        contentPadding = PaddingValues(Dimens.MarginSmall8),
//        border =  BorderStroke(Dimens.Border1, Colors.OrangeTransparent20),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Colors.Orange
        ),
        modifier = modifier.height(Dimens.ButtonToCartSmall32)
    ) {
        Text(
            text = text,
            style = Typography.CartButtonSmallTextStyle,
            color = Colors.Orange
        )
    }
}
