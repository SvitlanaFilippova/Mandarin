package com.mandarinkafe.mandarin.features.more.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.mandarinkafe.mandarin.core.domain.api.MenuMetaCache
import com.mandarinkafe.mandarin.features.more.domain.api.GetAppStoresUseCase
import com.mandarinkafe.mandarin.shared.device.AppVersionProvider
import com.mandarinkafe.mandarin.util.Resource
import com.mandarinkafe.mandarin.util.presentation.BaseViewModel
import kotlinx.coroutines.launch

class AboutViewModel(
    private val menuMetaCache: MenuMetaCache,
    private val appVersionProvider: AppVersionProvider,
    private val getAppStoresUseCase: GetAppStoresUseCase,
) :
    BaseViewModel<AboutContract.AboutEvent, AboutContract.AboutEffect, AboutContract.AboutState>() {
    init {
        getInitData()
        loadAppStores()
    }

    override fun setInitialState() = AboutContract.AboutState()

    private fun getInitData() {
        viewModelScope.launch {
            menuMetaCache.metaFlow.collect { meta ->
                setState {
                    copy(
                        versionName = appVersionProvider.getVersionName(),
                        lastUpdated = meta?.lastUpdated,
                        revision = meta?.revision
                    )
                }
            }
        }
    }

    private fun loadAppStores() {
        viewModelScope.launch {
            when (val result = getAppStoresUseCase()) {
                is Resource.Success -> {
                    setState {
                        copy(appStores = result.data ?: emptyList())
                    }
                }

                else -> {
                    // В случае ошибки оставляем пустой список
                    setState {
                        copy(appStores = emptyList())
                    }
                }
            }
        }
    }

    override fun onEvent(event: AboutContract.AboutEvent) {
        // не актуально
    }

    override fun setLoading(isLoading: Boolean) {
        // не актуально
    }
}
