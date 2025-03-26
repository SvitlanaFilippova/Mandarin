package com.mandarinkafe.mandarin.menu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

@Composable
fun MenuHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimens.ToolbarHeadHeight56),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.padding(Dimens.MarginSmall8),
            painter = painterResource(R.drawable.logo_text_mandarin),
            contentDescription = "Mandarin",
        )
    }
}