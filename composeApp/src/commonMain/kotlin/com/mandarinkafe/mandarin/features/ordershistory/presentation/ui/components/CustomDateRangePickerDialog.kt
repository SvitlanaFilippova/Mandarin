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
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.ordershistory.presentation.models.DateRange
import dev.icerock.moko.resources.compose.stringResource
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CustomDateRangePickerDialog(
    initialRange: DateRange?,
    onDismiss: () -> Unit,
    onConfirm: (DateRange) -> Unit
) {
    fun Long?.toDateText(placeholder: String): String =
        this?.let {
            val localDate = Instant.fromEpochMilliseconds(it)
                .toLocalDateTime(TimeZone.UTC)
                .date
            "${localDate.day.toString().padStart(2, '0')}-${localDate.month.number.toString().padStart(2, '0')}-${localDate.year.toString().takeLast(2)}"
        } ?: placeholder

    val datePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = initialRange?.start?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds(),
        initialSelectedEndDateMillis = initialRange?.end?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val (start, end) = datePickerState.selectedStartDateMillis to datePickerState.selectedEndDateMillis
                if (start != null && end != null) {
                    onConfirm(
                        DateRange(
                            start = Instant.fromEpochMilliseconds(start)
                                .toLocalDateTime(TimeZone.UTC).date,
                            end = Instant.fromEpochMilliseconds(end)
                                .toLocalDateTime(TimeZone.UTC).date
                        )
                    )
                }
            }) { Text(stringResource(MR.strings.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(MR.strings.cancel)) }
        }
    ) {
        DateRangePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors().copy(containerColor = Colors.DarkGrey),
            title = {
                Text(
                    stringResource(MR.strings.choose_period),
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
                            .toDateText(stringResource(MR.strings.filter_by_date_start)),
                        style = Typography.RegularTextStyle,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = datePickerState.selectedEndDateMillis
                            .toDateText(stringResource(MR.strings.filter_by_date_end)),
                        style = Typography.RegularTextStyle,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        )
    }
}