package com.mandarinkafe.mandarin.features.infrastructure.data.impl

import com.mandarinkafe.mandarin.core.data.network.IikoNetworkClient
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.AliveTerminalGroupsResponse
import com.mandarinkafe.mandarin.features.infrastructure.data.network.dto.TerminalGroupsIdsResponse
import com.mandarinkafe.mandarin.features.infrastructure.domain.api.AliveTerminalRepository
import com.mandarinkafe.mandarin.util.Constants
import com.mandarinkafe.mandarin.util.Resource

class AliveTerminalRepositoryImpl(
    private val networkClient: IikoNetworkClient
) : AliveTerminalRepository {

    override suspend fun checkAliveTerminals(): Resource<Boolean> {
        // Получаем все группы терминалов
        val groupsResponse = networkClient.getTerminalGroupsIds()
        if (groupsResponse.resultCode != Constants.HTTP_SUCCESS) {
            return Resource.ErrorOther("Ошибка получения терминальных групп")
        }

        val terminalGroupIds = (groupsResponse as? TerminalGroupsIdsResponse)
            ?.terminalGroups
            ?.flatMap { it.items }
            ?.map { it.id }
            .orEmpty()

        if (terminalGroupIds.isEmpty()) {
            return Resource.Success(false) // Нет терминалов — живых тоже нет
        }

        // Проверяем статус терминалов
        val aliveResponse = networkClient.getAliveTerminalGroups(terminalGroupIds)
        if (aliveResponse.resultCode != Constants.HTTP_SUCCESS) {
            return Resource.ErrorOther("Ошибка проверки статуса терминалов")
        }

        // Если хоть один "живой" - возвращаем true
        val isAlive = (aliveResponse as? AliveTerminalGroupsResponse)
            ?.isAliveStatus
            ?.any { it.isAlive } == true

        return Resource.Success(isAlive)
    }
}