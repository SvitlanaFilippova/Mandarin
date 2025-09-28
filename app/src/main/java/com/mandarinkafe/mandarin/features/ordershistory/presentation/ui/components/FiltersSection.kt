package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.features.order.domain.models.DeliveryType
import com.mandarinkafe.mandarin.features.ordershistory.presentation.models.DateFilterType
import com.mandarinkafe.mandarin.features.ordershistory.presentation.models.DateRange

@Composable
fun FiltersSection(
    chosenOrderTypes: List<DeliveryType>,
    chosenDateFilter: DateFilterType?,
    onOrderTypesChange: (List<DeliveryType>) -> Unit,
    onDateFilterChange: (DateFilterType?) -> Unit,
    chosenDateRange: DateRange?,
    onCustomRangeChange: (DateRange) -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.MarginSmall8),
        horizontalArrangement = Arrangement.spacedBy(Dimens.MarginSmall8)
    ) {
        FilterByDateChipDropdown(
            selectedItem = chosenDateFilter,
            allItems = DateFilterType.entries.toList(),
            onSelectionChange = onDateFilterChange,
            chosenDateRange = chosenDateRange,
            onCustomRangeChange = onCustomRangeChange,
        )

        FilterByTypeChipDropdown(
            selectedItems = chosenOrderTypes,
            allItems = DeliveryType.entries.toList(),
            onSelectionChange = onOrderTypesChange
        )
    }

}
