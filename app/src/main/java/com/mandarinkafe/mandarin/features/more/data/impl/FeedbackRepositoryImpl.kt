package com.mandarinkafe.mandarin.features.more.data.impl

import com.mandarinkafe.mandarin.BuildConfig
import com.mandarinkafe.mandarin.features.more.data.network.TelegramApi
import com.mandarinkafe.mandarin.features.more.domain.api.FeedbackRepository
import com.mandarinkafe.mandarin.features.more.domain.models.Feedback
import com.mandarinkafe.mandarin.util.Result
import javax.inject.Inject

class FeedbackRepositoryImpl @Inject constructor(
    private val telegramApi: TelegramApi
) : FeedbackRepository {

    override suspend fun sendFeedback(feedback: Feedback): Result<Unit> {
        return try {
            val formattedPhone = formatPhone(feedback.phone)

            val text = buildString {
                append(FEEDBACK_TITLE)
                append(NAME_LABEL).append(feedback.name).append("\n")
                append(PHONE_LABEL).append(formattedPhone).append("\n")
                append(EMAIL_LABEL).append(feedback.email).append("\n")
                append(MESSAGE_LABEL).append(feedback.message).append("\n")
                if (feedback.needAnswer) append(NEED_ANSWER_LABEL)
            }

            val url = TELEGRAM_API_URL.format(TELEGRAM_BOT_TOKEN)

            telegramApi.sendMessage(
                url = url,
                chatId = TELEGRAM_CHANNEL_ID,
                text = text
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    private fun formatPhone(phone: String): String {
        val digits = phone.filter { it.isDigit() }

        return when {
            digits.length == PHONE_LENGTH_10 -> {
                "+7 (${digits.substring(0, 3)}) " +
                        "${digits.substring(3, 6)}-" +
                        "${digits.substring(6, 8)}-" +
                        digits.substring(8, 10)
            }

            digits.length == PHONE_LENGTH_11 && (digits.startsWith(PHONE_PREFIX_7) || digits.startsWith(
                PHONE_PREFIX_8
            )) -> {
                "+7 (${digits.substring(1, 4)}) " +
                        "${digits.substring(4, 7)}-" +
                        "${digits.substring(7, 9)}-" +
                        digits.substring(9, 11)
            }

            else -> phone
        }
    }

    companion object {
        private const val PHONE_LENGTH_10 = 10
        private const val PHONE_LENGTH_11 = 11
        private const val PHONE_PREFIX_7 = "7"
        private const val PHONE_PREFIX_8 = "8"

        private const val TELEGRAM_BOT_TOKEN = BuildConfig.TG_BOT_TOKEN
        private const val TELEGRAM_CHANNEL_ID = BuildConfig.TG_CHANNEL_ID

        private const val TELEGRAM_API_URL = "https://api.telegram.org/bot%s/sendMessage"

        private const val FEEDBACK_TITLE = "<b>Новое обращение</b>\n\n"
        private const val NAME_LABEL = "\uD83D\uDC64 Имя: "
        private const val PHONE_LABEL = "\uD83D\uDCDE Телефон: "
        private const val EMAIL_LABEL = "\uD83D\uDCE7 Email: "
        private const val MESSAGE_LABEL = "\uD83D\uDCAC️ Сообщение: "
        private const val NEED_ANSWER_LABEL = "\n❗ Просит обратную связь ❗"
    }
}