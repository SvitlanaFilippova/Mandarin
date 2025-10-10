package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.shared.ui.viewmodel.SharedContract.SharedEvent

@Composable
fun AppTopBar(
    onEvent: (SharedEvent) -> Unit,
    showAppBar: Boolean,
) {
    if (showAppBar) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .height(Dimens.ToolbarHeadHeight40)
        ) {
            // Логотип
            Image(
                painter = painterResource(R.drawable.logo_text_mandarin),
                contentDescription = stringResource(R.string.logo_cafe),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(Dimens.MarginSuperSmall4)
                    .clickable { onEvent(SharedEvent.OnLogoClick) }
            )

            // Иконка звонка
            Box(
                modifier = Modifier
                    .size(Dimens.ToolbarHeadHeight40)
                    .align(Alignment.CenterEnd)
                    .padding(end = Dimens.MarginSmall8)
                    .clickable { onEvent(SharedEvent.OnPhoneClick) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_call),
                    tint = Colors.White,
                    contentDescription = stringResource(R.string.placeholder_call),
                    modifier = Modifier
                        .size(Dimens.IconSize24)
                        .align(Alignment.Center)
                )
            }
        }
    }
}