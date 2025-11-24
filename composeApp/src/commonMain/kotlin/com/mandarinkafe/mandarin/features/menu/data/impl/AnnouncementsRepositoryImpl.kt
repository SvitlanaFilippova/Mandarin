package com.mandarinkafe.mandarin.features.menu.data.impl

import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.features.menu.data.dto.AnnouncementsResponse
import com.mandarinkafe.mandarin.features.menu.domain.api.AnnouncementsRepository
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.applyTypography
import io.github.aakira.napier.Napier
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AnnouncementsRepositoryImpl(
    private val networkClient: ServerNetworkClient,
) : AnnouncementsRepository {

    private var announcementsCache: List<String>? = null
    private var lastRefreshTime: Long = 0

    /** Загрузка и кэширование объявлений из API */
    @OptIn(ExperimentalTime::class)
    override suspend fun loadAnnouncements(): Resource<Unit> {
        val response = try {
            networkClient.getAnnouncements()
        } catch (e: Exception) {
            return Resource.ErrorOther("Ошибка сети: ${e.message}")
        }

        if (response.resultCode == NO_CONNECTION) {
            return Resource.ErrorNoInternet()
        }

        val announcementsList = (response as? AnnouncementsResponse)?.data
        if (announcementsList.isNullOrEmpty()) {
            announcementsCache = emptyList()
            return Resource.Success(Unit)
        }

        // Фильтруем по датам и извлекаем только текст актуальных объявлений
        val currentTime = Clock.System.now()
        val validAnnouncements = announcementsList
            .filter { announcement ->
                isAnnouncementActive(announcement.startTime, announcement.endTime, currentTime)
            }
            .map { announcement ->
                announcement.text.applyTypography()
            }
            .filter { it.isNotBlank() }

        announcementsCache = validAnnouncements
        lastRefreshTime = Clock.System.now().toEpochMilliseconds()
        return Resource.Success(Unit)
    }

    /** Получение объявлений с проверкой кэша */
    override suspend fun getAnnouncements(): Resource<List<String>> {
        if (announcementsCache == null) {
            val result = loadAnnouncements()
            if (result !is Resource.Success) {
                return when (result) {
                    is Resource.ErrorNoInternet -> Resource.ErrorNoInternet()
                    is Resource.ErrorEmptyData -> Resource.ErrorEmptyData()
                    is Resource.ErrorOther -> Resource.ErrorOther(result.message.orEmpty())
                    else -> Resource.ErrorOther("Неизвестная ошибка при загрузке объявлений")
                }
            }
        }

        return Resource.Success(announcementsCache ?: emptyList())
    }

    /**
     * Загружает объявления только если они устарели (прошло больше 5 минут с последнего обновления)
     */
    @OptIn(ExperimentalTime::class)
    override suspend fun loadAnnouncementsIfStale(): Resource<Unit> {
        val now = Clock.System.now().toEpochMilliseconds()
        val timeSinceLastRefresh = now - lastRefreshTime

        // Обновляем только если прошло больше 5 минут с последнего обновления
        if (timeSinceLastRefresh > REFRESH_INTERVAL_MS) {
            return loadAnnouncements()
        }

        // Если данные свежие, просто возвращаем успех
        return Resource.Success(Unit)
    }

    /**
     * Проверяет, является ли объявление актуальным на текущий момент
     */
    @OptIn(ExperimentalTime::class)
    private fun isAnnouncementActive(
        startTime: String,
        endTime: String,
        currentTime: kotlin.time.Instant
    ): Boolean {
        return try {
            val start = parseInstant(startTime)
            val end = parseInstant(endTime)

            if (start == null || end == null) {
                // Если не удалось распарсить даты, считаем объявление активным
                // (сервер уже фильтрует, но на всякий случай)
                Napier.w("AnnouncementsRepository: не удалось распарсить даты: start=$startTime, end=$endTime")
                return true
            }

            currentTime >= start && currentTime <= end
        } catch (e: Exception) {
            Napier.e("AnnouncementsRepository: ошибка при проверке дат объявления", e)
            // В случае ошибки считаем объявление активным
            true
        }
    }

    /**
     * Парсит ISO 8601 строку в Instant
     */
    @OptIn(ExperimentalTime::class)
    private fun parseInstant(dateTimeString: String): kotlin.time.Instant? {
        return try {
            kotlin.time.Instant.parse(dateTimeString)
        } catch (e: Exception) {
            Napier.e("AnnouncementsRepository: ошибка парсинга даты: $dateTimeString", e)
            null
        }
    }

    private companion object {
        const val REFRESH_INTERVAL_MS = 5 * 60 * 1000L // 5 минут
    }
}

