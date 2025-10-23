package com.mandarinkafe.mandarin.features.more.data.impl

import com.mandarinkafe.mandarin.shared.BuildKonfig
import com.mandarinkafe.mandarin.features.more.data.network.TelegramApi
import com.mandarinkafe.mandarin.features.more.domain.api.DevFeedbackRepository
import com.mandarinkafe.mandarin.features.more.domain.models.Feedback
import com.mandarinkafe.mandarin.shared.device.DeviceInfoProvider
import com.mandarinkafe.mandarin.util.Result

class DevFeedbackRepositoryImpl(
    private val telegramApi: TelegramApi,
    private val deviceInfoProvider: DeviceInfoProvider
) : DevFeedbackRepository {

    override suspend fun sendDevFeedback(feedback: Feedback): Result<Unit> {
        return try {
            val text = buildString {
                append(DEV_FEEDBACK_TITLE)
                append(deviceInfoProvider.getDeviceInfo())

                append(NAME_LABEL).append(feedback.name).append("\n")
                append(PHONE_LABEL).append(feedback.phone).append("\n")
                append(EMAIL_LABEL).append(feedback.email).append("\n")
                append(MESSAGE_LABEL).append(feedback.message).append("\n")

                if (feedback.needAnswer) append(NEED_ANSWER_LABEL)
            }

            val url = TELEGRAM_API_URL.replace("%s", TELEGRAM_BOT_TOKEN)
            telegramApi.sendMessage(url, DEV_TELEGRAM_CHANNEL_ID, text)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    companion object {
        private const val DEV_FEEDBACK_TITLE = "<b>Новое обращение из приложения Мандарина</b>\n\n"
        private val TELEGRAM_BOT_TOKEN = BuildKonfig.TG_BOT_TOKEN
        private val DEV_TELEGRAM_CHANNEL_ID = BuildKonfig.DEV_TG_CHAT_ID
        private const val TELEGRAM_API_URL = "https://api.telegram.org/bot%s/sendMessage"

        private const val NAME_LABEL = "\uD83D\uDC64 Имя: "
        private const val PHONE_LABEL = "\uD83D\uDCDE Телефон: "
        private const val EMAIL_LABEL = "\uD83D\uDCE7  Email/Telegram: "
        private const val MESSAGE_LABEL = "\uD83D\uDCAC️ Сообщение:\n"
        private const val NEED_ANSWER_LABEL = "\n❗ Просит обратную связь ❗"

    }
}