package com.mandarinkafe.mandarin.features.more.domain.api

import com.mandarinkafe.mandarin.features.more.domain.models.Feedback
import com.mandarinkafe.mandarin.util.Result

interface DevFeedbackRepository {
    suspend fun sendDevFeedback(feedback: Feedback): Result<Unit>
}