package com.mandarinkafe.mandarin.features.menu.data.impl

import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.features.menu.data.dto.AnnouncementsResponse
import com.mandarinkafe.mandarin.features.menu.domain.api.AnnouncementsRepository
import com.mandarinkafe.mandarin.util.Constants.MENU_REMOTE_CACHE_STALE_INTERVAL_MS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Constants.ORDER_ACCEPT_LOCAL_TIME_ZONE_ID
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.applyTypography
import io.github.aakira.napier.Napier
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
        if (timeSinceLastRefresh > MENU_REMOTE_CACHE_STALE_INTERVAL_MS) {
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
        currentTime: kotlin.time.Instant,
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
     * Парсит дату в [Instant]: полный ISO‑8601 с `Z` или оффсетом — через [Instant.parse];
     * строка **без** зоны (`2026-04-03T11:00:00`) — как локальное время в [ORDER_ACCEPT_LOCAL_TIME_ZONE_ID].
     */
    @OptIn(ExperimentalTime::class)
    private fun parseInstant(dateTimeString: String): Instant? {
        val trimmed = dateTimeString.trim()
        if (trimmed.isEmpty()) return null
        return try {
            Instant.parse(trimmed)
        } catch (_: Exception) {
            parseLocalDateTimeAsInstantInCafeZone(trimmed)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun parseLocalDateTimeAsInstantInCafeZone(localIso: String): Instant? {
        return try {
            val local = LocalDateTime.parse(localIso)
            val zone = TimeZone.of(ORDER_ACCEPT_LOCAL_TIME_ZONE_ID)
            val kxInstant = local.toInstant(zone)
            Instant.fromEpochMilliseconds(kxInstant.toEpochMilliseconds())
        } catch (e: Exception) {
            Napier.e("AnnouncementsRepository: ошибка парсинга даты: $localIso", e)
            null
        }
    }

}

