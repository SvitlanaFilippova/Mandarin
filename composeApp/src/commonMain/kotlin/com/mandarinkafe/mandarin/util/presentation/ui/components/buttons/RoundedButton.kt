package com.mandarinkafe.mandarin.util.presentation.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    image: ImageVector? = null,
    contentDescription: String,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.CornerRadius8))
            .background(color = Colors.AppBlack80)
            .size(Dimens.ButtonBoxBig40),
        onClick = onClick,
    ) {
        image?.let {
            Icon(
                modifier = Modifier
                    .padding(Dimens.MarginSmall8),
                imageVector = image,
                tint = Colors.White,
                contentDescription = contentDescription
            )
        }
        painter?.let {
            Icon(
                modifier = Modifier
                    .padding(Dimens.MarginSmall8),
                painter = painter,
                tint = Colors.White,
                contentDescription = contentDescription
            )
        }
    }
}


