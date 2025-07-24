package com.mandarinkafe.mandarin.features.address.savedadresses.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.order.presentation.models.UiAddress

@Composable
fun SavedAddressesList(list: List<UiAddress>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)) {
        items(items = list) { item ->
            SavedAddressCard(
                selected = true,
                address = item,
                onAddressChosen = { },
                onEditAddress = {}
            )
        }
    }
}