package com.mandarinkafe.mandarin.features.infrastructure.data.impl

import com.mandarinkafe.mandarin.core.data.network.ServerNetworkClient
import com.mandarinkafe.mandarin.features.infrastructure.data.network.TerminalStatusServerResponse
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.AliveTerminalRepository
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Resource

class AliveTerminalRepositoryImpl(
    private val networkClient: ServerNetworkClient,
) : AliveTerminalRepository {

    override suspend fun checkAliveTerminals(): Resource<Boolean> {
        val response = networkClient.getTerminalStatus()
        if (response.resultCode == NO_CONNECTION) {
            return Resource.ErrorNoInternet()
        }
        if (response.resultCode != Constants.HTTP_SUCCESS) {
            return Resource.ErrorOther(
                "Ошибка проверки статуса терминалов"
            )
        }

        val isAlive = (response as? TerminalStatusServerResponse)?.isAlive == true
        return Resource.Success(isAlive)
    }
}
