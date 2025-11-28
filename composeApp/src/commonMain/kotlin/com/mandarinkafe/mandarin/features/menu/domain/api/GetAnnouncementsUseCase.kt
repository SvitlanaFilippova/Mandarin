package com.mandarinkafe.mandarin.features.menu.domain.api

import com.mandarinkafe.mandarin.util.Resource

interface GetAnnouncementsUseCase {
    suspend operator fun invoke(): Resource<List<String>>
}


