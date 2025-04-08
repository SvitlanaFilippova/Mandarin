package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Colors
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

@Composable
fun MenuTopBar(onSearchClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.ToolbarHeadHeight56),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search), tint = Colors.White,
            contentDescription = "поиск",
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    onClick = { onSearchClick() }
                )
                .padding(8.dp)
        )
        Image(
            modifier = Modifier.padding(Dimens.MarginSmall8),
            painter = painterResource(R.drawable.logo_text_mandarin),
            contentDescription = stringResource(R.string.logo_cafe),
        )
    }
}