package com.mandarinkafe.mandarin.features.more.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.presentation.theme.Colors
import com.mandarinkafe.mandarin.core.presentation.theme.Dimens
import com.mandarinkafe.mandarin.core.presentation.theme.Typography
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackEvent
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackViewModel
import com.mandarinkafe.mandarin.features.order.presentation.ui.components.MaskVisualTransformation
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.presentation.ui.components.CheckboxWithTextRow
import com.mandarinkafe.mandarin.util.presentation.ui.components.ConsentTextWithLinks
import com.mandarinkafe.mandarin.util.presentation.ui.components.MyTextField
import kotlinx.coroutines.flow.collectLatest

@Composable
fun FeedbackDialog(
    onDismissRequest: () -> Unit,
    viewModel: FeedbackViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effectFlow = viewModel.effect
    val onEvent = viewModel::onEvent

    // Локальное состояние ошибки
    var isError by remember { mutableStateOf(false) }

    // Валидация
    val isMessageValid = state.message.isNotBlank()
    val isContactValid = with(state) {
        !needAnswer || phone.length == Constants.VALID_PHONE_LENGTH || email.isNotBlank()
    }

    val isFormValid = isMessageValid && isContactValid

    val colors = TextFieldDefaults.colors(
        cursorColor = Colors.Orange,
        focusedTextColor = Colors.White,
        focusedContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        focusedIndicatorColor = Colors.White,
        unfocusedTextColor = Colors.White,
        unfocusedContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        unfocusedIndicatorColor = Colors.Transparent,
        errorIndicatorColor = Colors.ErrorRed,
        errorContainerColor = Colors.LightGrey.copy(alpha = 0.2f),
        disabledTextColor = Colors.White,
        disabledContainerColor = Colors.DarkGrey,
        disabledIndicatorColor = Colors.Transparent,
    )

    // локальные состояния для диалога после отправки формы
    var dialogMessage by remember { mutableStateOf<String?>(null) }
    val successMessage = stringResource(R.string.message_sent_successfully)
    var isSuccess by remember { mutableStateOf(false) }

    // форма обратной связи
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.more_message_manager)) },
        text = {
            Column {
                // Имя
                MyTextField(
                    value = state.name,
                    labelRes = R.string.your_name,
                    onValueChange = { onEvent(FeedbackEvent.SetName(it)) },
                    colors = colors
                )

                Spacer(Modifier.height(Dimens.MarginSmall8))

                // Номер телефона
                PhoneField(
                    value = state.phone,
                    isError = !isContactValid && isError,
                    onValueChange = { onEvent(FeedbackEvent.SetPhone(it)) },
                    colors = colors
                )

                // E-mail
                MyTextField(
                    value = state.email,
                    labelRes = R.string.your_email,
                    isError = !isContactValid && isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    onValueChange = { onEvent(FeedbackEvent.SetEmail(it)) },
                    colors = colors
                )

                Spacer(Modifier.height(Dimens.MarginSmall8))

                // Сообщение
                MyTextField(
                    value = state.message,
                    onValueChange = { onEvent(FeedbackEvent.SetMessage(it)) },
                    labelRes = R.string.your_message,
                    isError = !isMessageValid && isError,
                    minLines = 4,
                    colors = colors
                )

                Spacer(Modifier.height(Dimens.MarginSmall8))

                CheckboxWithTextRow(
                    checked = state.needAnswer,
                    labelRes = R.string.i_need_feedback,
                    onCheckedChange = { onEvent(FeedbackEvent.SetNeedFeedback(it)) }
                )
                Spacer(Modifier.height(Dimens.MarginSmall8))

                if (state.needAnswer && !isContactValid && isError) {
                    Text(
                        text = stringResource(R.string.contacts_are_required),
                        style = Typography.ErrorTextStyle,
                        modifier = Modifier.padding(start = Dimens.MarginStandard16)
                    )
                    Spacer(Modifier.height(Dimens.MarginSmall8))
                }
                ConsentTextWithLinks(
                    modifier = Modifier.padding(start = Dimens.MarginStandard16),
                    buttonName = stringResource(R.string.send)
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    if (isFormValid) {
                        onEvent(FeedbackEvent.SubmitForm)
                    } else {
                        isError = true
                    }
                }
            ) {
                Text(stringResource(R.string.send))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

// Доп. диалог для сообщений (успех/ошибка)
    if (dialogMessage != null) {
        AlertDialog(
            onDismissRequest = {
                dialogMessage = null
                if (isSuccess) {
                    onDismissRequest()
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dialogMessage = null
                        if (isSuccess) {
                            onDismissRequest()
                        }
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            title = { Text(stringResource(R.string.more_message_manager)) },
            text = { Text(dialogMessage ?: "") }
        )
    }

    // Эффекты от ViewModel
    LaunchedEffect(Unit) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is FeedbackContract.FeedbackEffect.ShowError -> {
                    dialogMessage = effect.message
                    isSuccess = false
                }

                is FeedbackContract.FeedbackEffect.ShowSuccess -> {
                    dialogMessage = successMessage
                    isSuccess = true
                }
            }
        }
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
