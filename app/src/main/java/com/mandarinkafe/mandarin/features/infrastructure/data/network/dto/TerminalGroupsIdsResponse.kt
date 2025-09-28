package com.mandarinkafe.mandarin.features.infrastructure.data.network.dto

import com.mandarinkafe.mandarin.core.data.dto.Response

data class TerminalGroupsIdsResponse(
    val terminalGroups: List<TerminalGroupDto>,
) : Response()