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
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography

@Composable
fun CustomizeButtonWithText(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        contentPadding = PaddingValues(Dimens.MarginSmall8),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.Orange,
            contentColor = Color.White
        ),
        modifier = modifier.height(Dimens.ButtonToCartSmall32)
    ) {
        Text(
            text = stringResource(R.string.add_additionals),
            style = Typography.CartButtonSmallTextStyle,
        )
    }
}
