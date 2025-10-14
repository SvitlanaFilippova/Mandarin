package com.mandarinkafe.mandarin.features.more.data.impl

import com.mandarinkafe.mandarin.core.data.config.ApiKeys
import com.mandarinkafe.mandarin.features.more.data.network.TelegramApi
import com.mandarinkafe.mandarin.features.more.domain.api.FeedbackRepository
import com.mandarinkafe.mandarin.features.more.domain.models.Feedback
import com.mandarinkafe.mandarin.util.Result

class FeedbackRepositoryImpl(
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

            val url = TELEGRAM_API_URL.replace("%s", TELEGRAM_BOT_TOKEN)

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
            digits.length == PHONE_LENGTH_10 -> formatWithGroups(digits, PHONE_10_GROUPS)
            digits.length == PHONE_LENGTH_11 && (digits.startsWith(PHONE_PREFIX_7) || digits.startsWith(
                PHONE_PREFIX_8
            )) ->
                formatWithGroups(digits, PHONE_11_GROUPS)

            else -> phone
        }
    }


    private fun formatWithGroups(digits: String, groups: Array<IntRange>): String {
        return "+7 (${digits.substring(groups[COUNTRY_CODE_INDEX])}) " +
                "${digits.substring(groups[FIRST_GROUP_INDEX])}-" +
                "${digits.substring(groups[SECOND_GROUP_INDEX])}-" +
                digits.substring(groups[THIRD_GROUP_INDEX])
    }

    // Расширение для substring по IntRange
    private fun String.substring(range: IntRange) = substring(range.first, range.last + 1)

    companion object {
        private const val COUNTRY_CODE_INDEX = 0
        private const val FIRST_GROUP_INDEX = 1
        private const val SECOND_GROUP_INDEX = 2
        private const val THIRD_GROUP_INDEX = 3

        private const val PHONE_LENGTH_10 = 10
        private const val PHONE_LENGTH_11 = 11
        private const val PHONE_PREFIX_7 = "7"
        private const val PHONE_PREFIX_8 = "8"

        private val TELEGRAM_BOT_TOKEN = ApiKeys.TG_BOT_TOKEN
        private val TELEGRAM_CHANNEL_ID = ApiKeys.TG_CHANNEL_ID

        private const val TELEGRAM_API_URL = "https://api.telegram.org/bot%s/sendMessage"

        private const val FEEDBACK_TITLE = "<b>Новое обращение</b>\n\n"
        private const val NAME_LABEL = "\uD83D\uDC64 Имя: "
        private const val PHONE_LABEL = "\uD83D\uDCDE Телефон: "
        private const val EMAIL_LABEL = "\uD83D\uDCE7 Email/Telegram: "
        private const val MESSAGE_LABEL = "\uD83D\uDCAC️ Сообщение:\n"
        private const val NEED_ANSWER_LABEL = "\n❗ Просит обратную связь ❗"

        // Индексы для форматирования телефона
        private val PHONE_10_GROUPS = arrayOf(0..2, 3..5, 6..7, 8..9)
        private val PHONE_11_GROUPS = arrayOf(1..3, 4..6, 7..8, 9..10)
    }
}