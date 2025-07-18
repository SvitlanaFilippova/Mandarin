package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField

@Composable
fun PersonalInfo(
    nameQuery: String,
    phoneQuery: String,
    onNameEntered: (String) -> Unit,
    onPhoneChanged: (String) -> Unit
) {
    val mask = MaskVisualTransformation(stringResource(R.string.phone_mask))
    MyTextField(
        value = nameQuery,
        labelRes = R.string.your_name,
        onValueChange = { onNameEntered(it) }
    )

    MyTextField(
        value = phoneQuery,
        labelRes = R.string.your_phone,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        onValueChange = { onPhoneChanged(it) },
        visualTransformation = mask
    )
}