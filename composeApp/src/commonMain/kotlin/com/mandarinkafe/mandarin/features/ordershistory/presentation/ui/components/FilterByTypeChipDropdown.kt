package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.order.presentation.models.toUi
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun FilterByTypeChipDropdown(
    selectedItems: List<DeliveryType>,
    allItems: List<DeliveryType>,
    onSelectionChange: (List<DeliveryType>) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val isSelected = remember(selectedItems) { selectedItems.isNotEmpty() }

    Column {
        FilterChip(
            selected = isSelected,
            onClick = { expanded = true },
            colors = FilterChipDefaults.filterChipColors()
                .copy(selectedContainerColor = Colors.Brown),
            label = {
                Text(
                    if (!isSelected) {
                        stringResource(MR.strings.filter_by_type)
                    } else {
                        selectedItems.map { stringResource(it.toUi().nameRes) }.joinToString(", ")
                    }
                )
            },
            leadingIcon = {
                if (isSelected) {
                    Icon(
                        painter = painterResource(MR.images.ic_close),
                        contentDescription = null,
                        tint = Colors.WhiteTransparent75,
                        modifier = Modifier.clickable(onClick = { onSelectionChange(emptyList()) })
                    )
                } else {
                    null
                }
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(MR.images.ic_arrow_drop_down),
                    contentDescription = null,
                    tint = Colors.WhiteTransparent75
                )

            }
        )
        DropdownMenu(
            expanded = expanded,
            containerColor = Colors.AppBlack,
            onDismissRequest = { expanded = false }
        ) {
            allItems.forEach { item ->
                val isSelected = item in selectedItems
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(item.toUi().nameRes),
                            color = Colors.WhiteTransparent75
                        )
                    },
                    onClick = {
                        val newSelection = if (isSelected) {
                            selectedItems - item
                        } else {
                            selectedItems + item
                        }
                        onSelectionChange(newSelection)
                    },
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                painter = painterResource(MR.images.ic_check),
                                tint = Colors.WhiteTransparent75,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    }
}
