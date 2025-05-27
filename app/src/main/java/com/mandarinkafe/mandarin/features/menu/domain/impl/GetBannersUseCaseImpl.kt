package com.mandarinkafe.mandarin.features.menu.domain.impl

import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository
import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.domain.usecase.GetBannersUseCase
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.Resource.Error
import com.mandarinkafe.mandarin.util.Resource.Idle
import com.mandarinkafe.mandarin.util.Resource.Loading
import com.mandarinkafe.mandarin.util.Resource.Success

class GetBannersUseCaseImpl(private val repository: BannersRepository) : GetBannersUseCase {
    override suspend fun invoke(): Resource<List<Banner>> {
        val result = repository.getBanners()

        return when (result) {
            is Success -> {
                val data = result.data.orEmpty()
                Success(data)
            }

            is Error -> {
                Error(result.message.toString())
            }

            is Loading -> {
                Loading()
            }

            is Idle -> Loading()
        }
    }
}
