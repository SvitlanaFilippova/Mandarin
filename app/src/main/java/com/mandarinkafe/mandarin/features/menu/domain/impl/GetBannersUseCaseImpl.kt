package com.mandarinkafe.mandarin.features.menu.domain.impl

import android.util.Log
import com.mandarinkafe.mandarin.features.menu.domain.api.BannersRepository

import com.mandarinkafe.mandarin.features.menu.domain.models.Banner
import com.mandarinkafe.mandarin.features.menu.domain.usecase.GetBannersUseCase
import com.mandarinkafe.mandarin.util.Resource

class GetBannersUseCaseImpl(private val repository: BannersRepository) : GetBannersUseCase {
    override suspend fun invoke(): Resource<List<Banner>> {
        Log.d("DEBUG BannersUC", "invoke(): calling repository.getBanners()")
        val result = repository.getBanners()
        Log.d("DEBUG BannersUC", "invoke(): repository returned $result")

        return when (result) {
            is Resource.Success -> {
                val data = result.data.orEmpty()
                Log.d("DEBUG BannersUC", "invoke(): success, mapped to domain, size=${data.size}")
                Resource.Success(data)
            }

            is Resource.Error -> {
                Log.e("DEBUG BannersUC", "invoke(): error = ${result.message}")
                Resource.Error(result.message.toString())
            }

            is Resource.Loading -> {
                Log.d("DEBUG BannersUC", "invoke(): loading")
                Resource.Loading()
            }
        }
    }
}
