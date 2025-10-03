package com.mandarinkafe.mandarin.util.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.MaskVisualTransformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun <Effect, Event> BaseFeedbackDialog(
    titleRes: Int,
    onDismissRequest: () -> Unit,
    effectFlow: Flow<Effect>,
    onEvent: (Event) -> Unit,
    submitEvent: Event,
    mapEffect: (Effect) -> Pair<String, Boolean>, // message, isSuccess
    name: String,
    phone: String,
    email: String,
    message: String,
    needAnswer: Boolean,
    isContactValid: Boolean,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onSetNeedFeedback: (Boolean) -> Unit,
    isFormValid: Boolean,
    isLoading: Boolean
) {
    var isError by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    stringResource(R.string.message_sent_successfully)

    val colors = TextFieldDefaults.colors(
        cursorColor = Colors.Orange,
        focusedTextColor = Colors.White,
        focusedContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        focusedIndicatorColor = Colors.White,
        unfocusedTextColor = Colors.White,
        unfocusedContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        unfocusedIndicatorColor = Colors.Transparent,
        errorIndicatorColor = Colors.Red,
        errorContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        disabledTextColor = Colors.White,
        disabledContainerColor = Colors.DarkGrey,
        disabledIndicatorColor = Colors.Transparent,
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(titleRes)) },
        text = {
            FeedbackFields(
                colors = colors,
                name = name,
                phone = phone,
                email = email,
                message = message,
                needAnswer = needAnswer,
                onNameChange = onNameChange,
                onPhoneChange = onPhoneChange,
                onEmailChange = onEmailChange,
                onMessageChange = onMessageChange,
                onSetNeedFeedback = onSetNeedFeedback,
                isContactValid = isContactValid,
                isError = isError,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isFormValid) {
                        onEvent(submitEvent)
                    } else {
                        isError = true
                    }
                },
                enabled = !isLoading // кнопка неактивна во время загрузки
            ) {
                if (isLoading) {
                    MyCircularProgressIndicator(
                        modifier = Modifier
                            .size(Dimens.ProgressBarSmallSize),
                        strokeWidth = Dimens.ProgressBarStroke6,
                    )
                }
                Text(stringResource(R.string.send))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    if (dialogMessage != null) {
        AlertDialog(
            onDismissRequest = {
                dialogMessage = null
                if (isSuccess) onDismissRequest()
            },
            confirmButton = {
                TextButton(onClick = {
                    dialogMessage = null
                    if (isSuccess) onDismissRequest()
                }) {
                    Text(stringResource(R.string.ok))
                }
            },
            title = { Text(stringResource(titleRes)) },
            text = { Text(dialogMessage ?: "") }
        )
    }

    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            val (msg, success) = mapEffect(effect)
            dialogMessage = msg
            isSuccess = success
        }
    }
}

@Composable
private fun FeedbackFields(
    colors: TextFieldColors,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onSetNeedFeedback: (Boolean) -> Unit,
    name: String,
    phone: String,
    email: String,
    message: String,
    needAnswer: Boolean,
    isContactValid: Boolean,
    isError: Boolean,
) {
    Column {
        // Имя
        MyTextField(
            value = name,
            labelRes = R.string.your_name,
            onValueChange = { onNameChange(it) },
            colors = colors
        )

        Spacer(Modifier.height(Dimens.MarginSmall8))

        // Номер телефона
        PhoneField(
            value = phone,
            isError = !isContactValid && isError,
            onValueChange = { onPhoneChange(it) },
            colors = colors
        )

        // E-mail
        MyTextField(
            value = email,
            labelRes = R.string.your_email,
            isError = !isContactValid && isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = { onEmailChange(it) },
            colors = colors
        )

        Spacer(Modifier.height(Dimens.MarginSmall8))

        // Сообщение
        MyTextField(
            value = message,
            onValueChange = { onMessageChange(it) },
            labelRes = R.string.your_message,
            isError = !message.isNotBlank() && isError,
            minLines = 4,
            colors = colors
        )

        Spacer(Modifier.height(Dimens.MarginSmall8))

        CheckboxWithTextRow(
            checked = needAnswer,
            labelRes = R.string.i_need_feedback,
            onCheckedChange = { onSetNeedFeedback(it) }
        )
        Spacer(Modifier.height(Dimens.MarginSmall8))

        if (needAnswer && !isContactValid && isError) {
            Text(
                text = stringResource(R.string.contacts_are_required),
                style = Typography.ErrorTextStyle,
                modifier = Modifier.padding(start = Dimens.MarginStandard16)
            )
            Spacer(Modifier.height(Dimens.MarginSmall8))
        }
        ConsentTextWithLinks(
            buttonName = stringResource(R.string.send)
        )
    }
}

@Composable
private fun PhoneField(
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors
) {
    val mask = MaskVisualTransformation(stringResource(R.string.phone_mask))

    MyTextField(
        value = value,
        labelRes = R.string.your_phone,
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        onValueChange = { onValueChange(it) },
        visualTransformation = mask,

        placeholder = {
            Text(
                text = stringResource(R.string.phone_placeholder),
                style = Typography.RegularLightTextStyle
            )
        },
        prefix = {
            Text(
                text = stringResource(R.string.phone_prefix),
                style = Typography.RegularTextStyle
            )
        },
        colors = colors
    )

    Spacer(Modifier.height(Dimens.MarginSmall8))
}
