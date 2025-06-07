package com.mandarinkafe.mandarin.util.presentation.ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens

@Composable
fun CustomizeButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.size(Dimens.ButtonBox32),
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        contentPadding = PaddingValues(Dimens.MarginSuperSmall4),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.OrangeTransparent20,
        ),

        ) {
        Icon(
            painter = painterResource(R.drawable.ic_additionals),
            contentDescription = stringResource(R.string.add_additionals),
            modifier = Modifier.size(Dimens.IconSize24),
            tint = Colors.White
        )
    }
}


