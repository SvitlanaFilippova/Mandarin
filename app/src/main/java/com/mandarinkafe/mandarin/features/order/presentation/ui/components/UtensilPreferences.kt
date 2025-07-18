package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.util.presentation.ui.components.CheckboxWithTextRow

@Composable
fun UtensilPreferences(
    noUtensils: Boolean,
    chosenUtensils: List<Utensil>,
    onChangeNoUtensils: (Boolean) -> Unit,
    onChooseUtensil: (Utensil, Boolean) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.MarginSmall8)
                .toggleable(
                    value = noUtensils,
                    role = Role.Switch,
                    onValueChange = { onChangeNoUtensils(it) }
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                modifier = Modifier.padding(horizontal = Dimens.Margin12),
                checked = noUtensils,
                onCheckedChange = null
            )
            Text(
                text = stringResource(R.string.no_utensils),
                style = Typography.RegularLightTextStyle,
                color = Colors.White,
                modifier = Modifier.padding(start = Dimens.MarginSmall8)
            )
        }

        if (!noUtensils) {
            val utensilTypes = remember { Utensil.entries.toList() }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.MarginStandard16),
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