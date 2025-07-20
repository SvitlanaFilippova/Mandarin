package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography

@Composable
fun IconWithTooltipInfo(tooltipTextResID: Int) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        // Иконка ⓘ
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = stringResource(R.string.why_is_that_number),
            modifier = Modifier
                .clickable { expanded = true }
                .padding(Dimens.MarginSmall8),
            tint = Colors.LightGrey
        )

        // Всплывающее окно c подсказкой
        DropdownMenu(
            expanded = expanded,
            containerColor = Colors.White.copy(alpha = 0.9f),
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 0.dp, y = Dimens.MarginSmall8),
        ) {
            Text(
                text = stringResource(tooltipTextResID),
                modifier = Modifier
                    .padding(Dimens.MarginSmall8)
                    .widthIn(max = Dimens.TooltipMaxWidth),
                style = Typography.SmallTextStyle.copy(color = Colors.AppBlack),
            )
        }
    }
}