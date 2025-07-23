package com.mandarinkafe.mandarin.features.location.presentation.ui.components

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
import com.mandarinkafe.mandarin.core.presentation.theme.Typography.ToCartButtonBigStyle

@Composable
fun BigButtonWithText(
    modifier: Modifier = Modifier,
    shouldBeActive: Boolean,
    textResID: Int,
    onMissingRequiredInfo: () -> Unit = {},
    onClick: () -> Unit,
) {
    val contentColor = if (shouldBeActive) {
        Color.White
    } else {
        Color.White.copy(alpha = 0.6f)
    }
    val containerColor = if (shouldBeActive) {
        Colors.Orange
    } else {
        Colors.LightGrey.copy(alpha = 0.6f)
    }

    val onClickAction = when {
        !shouldBeActive -> onMissingRequiredInfo
        else -> onClick
    }

    Button(
        modifier = modifier
            .height(Dimens.BigButtonWithTextHeight),
        onClick = onClickAction,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),

        ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
        Text(
            text = stringResource(textResID),
            style = ToCartButtonBigStyle,
            color = contentColor
        )
//        }
    }

}