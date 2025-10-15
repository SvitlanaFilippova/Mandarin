package com.mandarinkafe.mandarin.features.menu.data.dto

import com.mandarinkafe.mandarin.core.data.dto.Response
import kotlinx.serialization.Serializable

@Serializable
data class ServerMenuResponse(
    val lastUpdated: String, // время последнего ообновления меню из iiko
    val menu: MenuDataDTO,
) : Response()




