package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.util.presentation.ui.components.CheckboxWithTextRow
import com.mandarinkafe.mandarin.util.presentation.ui.components.SwitchWithTextRow

@Composable
fun UtensilPreferences(
    noUtensils: Boolean,
    chosenUtensils: List<Utensil>,
    onChangeNoUtensils: (Boolean) -> Unit,
    onChooseUtensil: (Utensil, Boolean) -> Unit
) {
    Column {
        SwitchWithTextRow(
            value = noUtensils,
            onValueChange = onChangeNoUtensils,
            textRes = R.string.no_utensils
        )

        if (!noUtensils) {
            val utensilTypes = remember { Utensil.entries.toList() }
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.MarginStandard16, bottom = Dimens.MarginSuperSmall4),
                    text = stringResource(R.string.i_need_utensils),
                    style = Typography.RegularTextStyle,
                )
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)) {
                    utensilTypes.forEach { item ->
                        CheckboxWithTextRow(
                            checked = chosenUtensils.contains(item),
                            labelRes = item.nameRes,
                            onCheckedChange = { checked -> onChooseUtensil(item, checked) }
                        )
                    }
                }
            }
        }
    }
}