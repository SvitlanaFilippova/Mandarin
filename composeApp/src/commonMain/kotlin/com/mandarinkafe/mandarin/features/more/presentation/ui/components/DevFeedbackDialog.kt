package com.mandarinkafe.mandarin.features.more.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DevFeedbackContract
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.DevFeedbackContract.DevFeedbackEvent
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberDevFeedbackViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs.BaseFeedbackDialog
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun DevFeedbackDialog(
    onDismissRequest: () -> Unit,
) {
    val viewModel = rememberDevFeedbackViewModel()
    val state by viewModel.state.collectAsState()
    val successTitle = stringResource(MR.strings.message_sent_successfully)

    with(state) {
        BaseFeedbackDialog(
            title = MR.strings.dev_feedback_form_title,
            onDismissRequest = onDismissRequest,
            effectFlow = viewModel.effect,
            onEvent = viewModel::onEvent,
            submitEvent = DevFeedbackEvent.SubmitForm,
            mapEffect = { effect ->
                when (effect) {
                    is DevFeedbackContract.DevFeedbackEffect.ShowError -> effect.message to false
                    is DevFeedbackContract.DevFeedbackEffect.ShowSuccess ->
                        successTitle to true
                }
            },
            name = name,
            phone = phone,
            email = email,
            message = message,
            needAnswer = needAnswer,
            isContactValid = isContactValid,
            isLoading = isLoading,
            onNameChange = { viewModel.onEvent(DevFeedbackEvent.SetName(it)) },
            onPhoneChange = { viewModel.onEvent(DevFeedbackEvent.SetPhone(it)) },
            onEmailChange = { viewModel.onEvent(DevFeedbackEvent.SetEmail(it)) },
            onMessageChange = { viewModel.onEvent(DevFeedbackEvent.SetMessage(it)) },
            onSetNeedFeedback = { viewModel.onEvent(DevFeedbackEvent.SetNeedFeedback(it)) },
            isFormValid = state.isFormValid
        )
    }
}
