package com.mandarinkafe.mandarin.features.more.domain.api

import com.mandarinkafe.mandarin.features.more.domain.models.Feedback
import com.mandarinkafe.mandarin.util.Result

interface FeedbackRepository {
    suspend fun sendFeedback(feedback: Feedback): Result<Unit>
}