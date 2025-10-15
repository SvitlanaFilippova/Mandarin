package com.mandarinkafe.mandarin.core.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CsvResponse(val csv: String?) : Response()




