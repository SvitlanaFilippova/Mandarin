package com.mandarinkafe.mandarin.features.ordershistory.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.ordershistory.presentation.models.DateRange
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateRangePickerDialog(
    initialRange: DateRange?,
    onDismiss: () -> Unit,
    onConfirm: (DateRange) -> Unit
) {
    fun Long?.toDateText(placeholder: String): String =
        this?.let {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yy")
            Instant.ofEpochMilli(it)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
                .format(formatter)
        } ?: placeholder

    val datePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialRange?.start?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()?.toEpochMilli(),
        initialSelectedEndDateMillis = initialRange?.end?.atStartOfDay(ZoneOffset.UTC)?.toInstant()
            ?.toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val (start, end) = datePickerState.selectedStartDateMillis to datePickerState.selectedEndDateMillis
                if (start != null && end != null) {
                    onConfirm(
                        DateRange(
                            start = Instant.ofEpochMilli(start).atZone(ZoneOffset.UTC)
                                .toLocalDate(),
                            end = Instant.ofEpochMilli(end).atZone(ZoneOffset.UTC).toLocalDate()
                        )
                    )
                }
            }) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    ) {
        DateRangePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors().copy(containerColor = Colors.DarkGrey),
            title = {
                Text(
                    stringResource(R.string.choose_period),
                    Modifier.padding(Dimens.MarginStandard16)
                )
            },
            headline = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(Dimens.MarginStandard16)
                ) {
                    Text(
                        text = datePickerState.selectedStartDateMillis
                            .toDateText(stringResource(R.string.filter_by_date_start)),
                        style = Typography.RegularTextStyle,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = datePickerState.selectedEndDateMillis
                            .toDateText(stringResource(R.string.filter_by_date_end)),
                        style = Typography.RegularTextStyle,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        )
    }
}