package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.util.presentation.ui.components.CheckboxWithTextRow
import com.mandarinkafe.mandarin.util.presentation.ui.components.textfields.MyTextField
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun PersonalInfo(
    nameQuery: String,
    onNameEntered: (String) -> Unit,
    saveName: Boolean,
    onSaveNameToggled: (Boolean) -> Unit,
) {
    MyTextField(
        value = nameQuery,
        labelRes = MR.strings.your_name,
        onValueChange = { onNameEntered(it) }
    )

    CheckboxWithTextRow(
        modifier = Modifier.padding(top = Dimens.MarginSmall8),
        checked = saveName,
        text = stringResource(MR.strings.save_name_for_future),
        onCheckedChange = { onSaveNameToggled(it) }
    )
}