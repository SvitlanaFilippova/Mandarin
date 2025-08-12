package com.mandarinkafe.mandarin.util.presentation.ui.components.buttons

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography.ToCartButtonStyle

@Composable
fun ButtonWithText(
    modifier: Modifier = Modifier,
    shouldBeActive: Boolean = true,
    text: String = "",
    @StringRes textResID: Int? = null,
    onMissingRequiredInfo: () -> Unit = {},
    onClick: () -> Unit,
    containerColor: Color = Colors.Orange
) {
    val contentColor = if (shouldBeActive) {
        Color.White
    } else {
        Color.White.copy(alpha = 0.6f)
    }
    val containerColor = if (shouldBeActive) {
        containerColor
    } else {
        Colors.LightGrey.copy(alpha = 0.6f)
    }

    val onClickAction = when {
        !shouldBeActive -> onMissingRequiredInfo
        else -> onClick
    }

    Button(
        modifier = modifier
            .height(Dimens.ButtonWithTextHeight),
        onClick = onClickAction,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
    ) {
        val text = if (textResID != null) stringResource(textResID) else text

        Text(
            text = text,
            style = ToCartButtonStyle,
            color = contentColor
        )

    }
}