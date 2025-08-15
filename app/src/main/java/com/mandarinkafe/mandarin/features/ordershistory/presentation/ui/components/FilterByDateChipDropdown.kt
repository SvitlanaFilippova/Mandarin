package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.features.ordershistory.presentation.models.DateFilterType
import com.mandarinkafe.mandarin.features.ordershistory.presentation.models.DateRange
import com.mandarinkafe.mandarin.features.ordershistory.presentation.models.toUi

@Composable
fun FilterByDateChipDropdown(
    selectedItem: DateFilterType?,
    chosenDateRange: DateRange?,
    allItems: List<DateFilterType>,
    onSelectionChange: (DateFilterType?) -> Unit,
    onCustomRangeChange: (DateRange) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showCustomRangeDialog by remember { mutableStateOf(false) }
    val isSelected = remember(selectedItem) { selectedItem != null }

    Column {
        FilterChip(
            selected = isSelected,
            onClick = { expanded = true },
            colors = FilterChipDefaults.filterChipColors()
                .copy(selectedContainerColor = Colors.Brown),
            label = {
                Text(
                    when {
                        selectedItem == null -> stringResource(R.string.filter_by_date)
                        selectedItem == DateFilterType.CUSTOM_RANGE && chosenDateRange != null ->
                            "${chosenDateRange.start} – ${chosenDateRange.end}"

                        else -> stringResource(selectedItem.toUi().nameRes)
                    }
                )
            },
            leadingIcon = {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Colors.WhiteTransparent75,
                        modifier = Modifier.clickable {
                            onSelectionChange(null)
                        }
                    )
                }
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Colors.WhiteTransparent75
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            containerColor = Colors.Brown,
            onDismissRequest = { expanded = false }
        ) {
            allItems.forEach { item ->
                val isSelectedItem = item == selectedItem
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(item.toUi().nameRes),
                            color = Colors.WhiteTransparent75
                        )
                    },
                    onClick = {
                        expanded = false
                        if (item == DateFilterType.CUSTOM_RANGE) {
                            showCustomRangeDialog = true
                        } else {
                            onSelectionChange(item)
                        }
                    },
                    trailingIcon = {
                        if (isSelectedItem) Icon(
                            Icons.Default.Check,
                            tint = Colors.WhiteTransparent75,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }

    if (showCustomRangeDialog) {
        CustomDateRangePickerDialog(
            initialRange = chosenDateRange,
            onDismiss = { showCustomRangeDialog = false },
            onConfirm = { range ->
                showCustomRangeDialog = false
                onCustomRangeChange(range)
                onSelectionChange(DateFilterType.CUSTOM_RANGE)
            }
        )
    }
}