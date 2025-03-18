package com.mandarinkafe.mandarin.menu.data


import android.util.Log
import com.mandarinkafe.mandarin.menu.data.dto.MenuResponse
import com.mandarinkafe.mandarin.menu.data.network.NetworkClient
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MenuRepositoryImpl(
    private val networkClient: NetworkClient,
    private val converter: DtoToDomainConverter
) : MenuRepository {
    override fun getMenu(): Flow<Resource<List<MealCategory>>> = flow {
        val response = networkClient.doRequest()
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету. "))
            }

            200 -> {
                val categories = (response as MenuResponse).itemCategories
                if (categories != null) {
                    emit(
                        Resource.Success(
                            converter.setMenuStructure(categories)
                        )
                    )
                } else {
                    Log.e("DEBUG", "response.itemCategories оказался null")
                    emit(Resource.Error("Сервер вернул пустые данные категорий"))
                }
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }

}
