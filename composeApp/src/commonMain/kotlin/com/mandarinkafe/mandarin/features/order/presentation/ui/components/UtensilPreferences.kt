package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.domain.models.Utensil
import com.mandarinkafe.mandarin.util.presentation.ui.components.CheckboxWithTextRow
import com.mandarinkafe.mandarin.util.presentation.ui.components.SwitchWithTextRow
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun UtensilPreferences(
    noUtensils: Boolean,
    chosenUtensils: List<Utensil>,
    onChangeNoUtensils: (Boolean) -> Unit,
    onChooseUtensil: (Utensil, Boolean) -> Unit,
) {
    Column {
        SwitchWithTextRow(
            value = noUtensils,
            onValueChange = onChangeNoUtensils,
            text = stringResource(MR.strings.no_utensils)
        )

        AnimatedVisibility(
            visible = !noUtensils,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val utensilTypes = remember { Utensil.entries.toList() }
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = Dimens.MarginStandard16,
                            bottom = Dimens.MarginSuperSmall4
                        ),
                    text = stringResource(MR.strings.i_need_utensils),
                    style = Typography.RegularTextStyle,
                )
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.MarginSuperSmall4)) {
                    utensilTypes.forEach { item ->
                        CheckboxWithTextRow(
                            checked = chosenUtensils.contains(item),
                            text = item.stringName,
                            onCheckedChange = { checked -> onChooseUtensil(item, checked) }
                        )
                    }
                }
            }
        }
    }
}
