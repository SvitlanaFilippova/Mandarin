package com.mandarinkafe.mandarin.menu.data.impl

import android.content.Context
import com.mandarinkafe.mandarin.R
import com.mandarinkafe.mandarin.core.data.network.NetworkClient
import com.mandarinkafe.mandarin.menu.data.dto.MenuResponse
import com.mandarinkafe.mandarin.menu.data.mapper.DtoToDomainConverter
import com.mandarinkafe.mandarin.menu.domain.api.MenuRepository
import com.mandarinkafe.mandarin.menu.domain.models.MealCategory
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class MenuRepositoryImpl @Inject constructor(
    private val networkClient: NetworkClient,
    private val converter: DtoToDomainConverter,
    @ApplicationContext private val context: Context
) : MenuRepository {

    private val _menu =
        MutableStateFlow<Resource<List<MealCategory>>>(Resource.Loading()) // Состояние загрузки
    override val menu: StateFlow<Resource<List<MealCategory>>> = _menu.asStateFlow()

    override fun getMenu(): Flow<Resource<List<MealCategory>>> {
        // если данные уже есть, возвращаем их
        if (_menu.value is Resource.Success) return menu

        // если данных нет, начинаем загрузку
        fetchMenuFromNetwork()
        return menu
    }

    // Метод для принудительного обновления
    override suspend fun forceRefresh() {
        fetchMenuFromNetwork(true)
    }

    private fun fetchMenuFromNetwork(force: Boolean = false) {
        // Если данных нет или нужно принудительное обновление
        if (_menu.value !is Resource.Success || force) {
            CoroutineScope(Dispatchers.IO).launch {
                _menu.value = Resource.Loading()
                try {
                    // Загружаем меню
                    val response = networkClient.getMenu()
                    when (response.resultCode) {
                        -1 -> _menu.value =
                            Resource.Error(context.getString(R.string.error_no_internet))

                        HTTP_SUCCESS -> {
                            val categories = (response as MenuResponse).itemCategories
                            if (categories != null) {
                                _menu.value =
                                    Resource.Success(converter.menuDtoToDomain(categories))
                            } else {
                                _menu.value =
                                    Resource.Error(context.getString(R.string.error_empty_menu))
                            }
                        }

                        else -> _menu.value =
                            Resource.Error(context.getString(R.string.error_server_error))
                    }
                } catch (e: Exception) {
                    _menu.value =
                        Resource.Error(context.getString(R.string.error_something_wrong, e.message))
                }
            }
        }
    }
}