package com.mandarinkafe.mandarin.util.presentation.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CustomizeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.size(Dimens.ButtonToCartSmall36),
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        contentPadding = PaddingValues(Dimens.MarginSuperSmall4),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.OrangeTransparent20,
        ),

        ) {
        Icon(
            painter = painterResource(MR.images.ic_additionals),
            contentDescription = stringResource(MR.strings.add_additionals),
            modifier = Modifier.size(Dimens.IconSize24),
            tint = Colors.White
        )
    }
}