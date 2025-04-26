package com.mandarinkafe.mandarin.menu.ui.components.meal_details_bottom_sheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.EditableType
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens
import com.mandarinkafe.mandarin.core.ui.theme.Typography

@Composable
fun OpenCustomizationButton(
    onClick: () -> Unit,
    editableType: EditableType,
    modifier: Modifier = Modifier
) {
    val buttonIcon =
        if (editableType == EditableType.PIZZA) {
            painterResource(R.drawable.ic_additionals)
        } else null

    val buttonText = when (editableType) {
        EditableType.PIZZA -> stringResource(id = R.string.add_additionals)
        EditableType.MODIFIABLE -> stringResource(id = R.string.choose)
        EditableType.WOK -> stringResource(id = R.string.create_own_box)
    }

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(Dimens.CornerRadius8),
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.Transparent,
            contentColor = Color.White,
        ),
        border = BorderStroke(width = Dimens.ButtonBorder1, color = Colors.White),
        modifier = modifier
            .fillMaxWidth()
    ) {

    Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.MarginStandard16)
        ) {
            if (buttonIcon != null) {
                Icon(
                    painter = buttonIcon,
                    contentDescription = stringResource(id = R.string.add_additionals),
                    tint = Color.White
                )
            }
            Text(
                text = buttonText,
                style = Typography.ToCartButtonBigStyle
            )
        }
    }
}