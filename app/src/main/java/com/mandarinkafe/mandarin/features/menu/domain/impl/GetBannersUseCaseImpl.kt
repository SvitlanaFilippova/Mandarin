package com.mandarinkafe.mandarin.features.menu.domain.impl

import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository

import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.domain.usecase.GetBannersUseCase
import com.mandarinkafe.mandarin.util.Resource

class GetBannersUseCaseImpl(private val repository: BannersRepository) : GetBannersUseCase {
    override suspend fun invoke(): Resource<List<Banner>> {
        val result = repository.getBanners()

        return when (result) {
            is Resource.Success -> {
                val data = result.data.orEmpty()
                Resource.Success(data)
            }

            is Resource.Error -> Resource.Error(result.message.toString())
            is Resource.Loading -> Resource.Loading()
        }
    }
}
