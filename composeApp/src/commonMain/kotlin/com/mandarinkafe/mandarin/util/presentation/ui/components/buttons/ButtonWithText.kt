package com.mandarinkafe.mandarin.util.presentation.ui.components.buttons

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography.ButtonTextStyle

@Composable
fun ButtonWithText(
    modifier: Modifier = Modifier,
    shouldBeActive: Boolean = true,
    text: String = "",
    onMissingRequiredInfo: () -> Unit = {},
    onClick: () -> Unit,
    containerColor: Color = Colors.Orange,
    contentColor: Color = Colors.White,
    disabledContainerColor: Color = Colors.LightGrey.copy(alpha = 0.8f),
) {
    val onClickAction = when {
        !shouldBeActive -> onMissingRequiredInfo
        else -> onClick
    }

    Button(
        modifier = modifier
            .heightIn(min = Dimens.ButtonWithTextHeight),
        onClick = onClickAction,
        enabled = shouldBeActive,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor
        ),
    ) {
        Text(
            text = text,
            style = ButtonTextStyle,
            color = contentColor
        )
    }
}


