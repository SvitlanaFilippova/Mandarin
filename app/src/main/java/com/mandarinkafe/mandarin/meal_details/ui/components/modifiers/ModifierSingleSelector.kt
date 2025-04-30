package com.mandarinkafe.mandarin.meal_details.ui.components.modifiers

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.domain.models.ModifierItem
import com.mandarinkafe.mandarin.core.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifierSingleSelector(
    items: List<ModifierItem>,
    selectedItem: ModifierItem?,
    onItemSelected: (ModifierItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val textFieldValue = if (selectedItem != null) {
        stringResource(
            R.string.meal_title_with_price_template,
            selectedItem.name,
            selectedItem.price
        )
    } else ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        // Поле выбора
        TextField(
            value = textFieldValue,
            onValueChange = {},
            readOnly = true,
            shape = RoundedCornerShape(Dimens.CornerRadius8),
            label = { Text(stringResource(R.string.to_choose)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        // Выпадающий список
        ExposedDropdownMenu(
            shape = RoundedCornerShape(Dimens.CornerRadius8),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->

                val itemText = stringResource(
                    R.string.meal_title_with_price_template,
                    item.name,
                    item.price
                )

                DropdownMenuItem(
                    text = { Text(itemText) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    trailingIcon = {
                        if (item == selectedItem) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = stringResource(R.string.selected)
                            )
                        }
                    }
                )
            }
        }
    }
}