package com.mandarinkafe.mandarin.features.order.presentation.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField

@Composable
fun PersonalInfo(
    nameQuery: String,
    phoneQuery: String,
    isError: Boolean,
    phoneIsValid: Boolean,
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
        isError = isError && !phoneIsValid,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = { onPhoneChanged(it.filter { it.isDigit() }) },
        visualTransformation = mask,
        placeholder = {
            Text(
                text = stringResource(R.string.phone_placeholder),
                style = Typography.RegularLightTextStyle
            )
        },
    )
}