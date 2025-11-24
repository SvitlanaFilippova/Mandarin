package com.mandarinkafe.mandarin.features.menu.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface AnnouncementsRepository {
    suspend fun getAnnouncements(): Resource<List<String>>
    suspend fun loadAnnouncements(): Resource<Unit>
    suspend fun loadAnnouncementsIfStale(): Resource<Unit>
}

