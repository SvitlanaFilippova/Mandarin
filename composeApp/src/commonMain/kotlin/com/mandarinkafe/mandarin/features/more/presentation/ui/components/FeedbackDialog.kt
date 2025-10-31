package com.mandarinkafe.mandarin.features.more.presentation.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.mandarinkafe.mandarin.MR
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract
import com.mandarinkafe.mandarin.features.more.presentation.viewmodel.FeedbackContract.FeedbackEvent
import com.mandarinkafe.mandarin.shared.presentation.viewmodel.rememberFeedbackViewModel
import com.mandarinkafe.mandarin.util.presentation.ui.components.dialogs.BaseFeedbackDialog
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun FeedbackDialog(
    onDismissRequest: () -> Unit,

    ) {
    val viewModel = rememberFeedbackViewModel()
    val state by viewModel.state.collectAsState()
    val successTitle = stringResource(MR.strings.message_sent_successfully)

    with(state) {
        BaseFeedbackDialog(
            title = MR.strings.more_message_manager,
            onDismissRequest = onDismissRequest,
            effectFlow = viewModel.effect,
            onEvent = viewModel::onEvent,
            submitEvent = FeedbackEvent.SubmitForm,
            mapEffect = { effect ->
                when (effect) {
                    is FeedbackContract.FeedbackEffect.ShowError -> effect.message to false
                    is FeedbackContract.FeedbackEffect.ShowSuccess ->
                        successTitle to true
                }
            },
            name = name,
            phone = phone,
            email = email,
            message = message,
            needAnswer = needAnswer,
            isContactValid = isContactValid,
            onNameChange = { viewModel.onEvent(FeedbackEvent.SetName(it)) },
            onPhoneChange = { viewModel.onEvent(FeedbackEvent.SetPhone(it)) },
            onEmailChange = { viewModel.onEvent(FeedbackEvent.SetEmail(it)) },
            onMessageChange = { viewModel.onEvent(FeedbackEvent.SetMessage(it)) },
            onSetNeedFeedback = { viewModel.onEvent(FeedbackEvent.SetNeedFeedback(it)) },
            isFormValid = isFormValid,
            isLoading = isLoading
        )
    }
}