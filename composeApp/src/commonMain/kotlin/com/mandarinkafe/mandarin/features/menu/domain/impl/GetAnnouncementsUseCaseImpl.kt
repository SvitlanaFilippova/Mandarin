package com.mandarinkafe.mandarin.features.menu.domain.impl

import com.mandarinkafe.mandarin.features.menu.domain.api.AnnouncementsRepository
import com.mandarinkafe.mandarin.features.menu.domain.api.GetAnnouncementsUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.Success

class GetAnnouncementsUseCaseImpl(private val repository: AnnouncementsRepository) : GetAnnouncementsUseCase {
    override suspend fun invoke(): Resource<List<String>> {
        val result = repository.getAnnouncements()

        return when (result) {
            is Success -> {
                val data = result.data.orEmpty()
                Success(data)
            }

            else -> {
                result
            }
        }
    }
}


