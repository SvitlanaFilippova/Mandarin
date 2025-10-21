package com.mandarinkafe.mandarin.features.menu.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.textfields.SearchBarInputField
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MenuSearchBar(
    onSearchClick: () -> Unit
) {
    SearchBarInputField(
        modifier = Modifier
            .padding(horizontal = Dimens.MarginSmall8)
            .clickable(onClick = onSearchClick)
            .background(color = Colors.AppBlack),
        query = "",
        placeholderText = stringResource(MR.strings.search_in_menu),
        enabled = false,
        leadingIcon = {
            Icon(
                painter = painterResource(MR.images.ic_search),
                contentDescription = stringResource(MR.strings.search_in_menu),
                tint = Colors.White
            )
        }
    )
}
