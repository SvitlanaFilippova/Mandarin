package com.mandarinkafe.mandarin.features.more.data

import com.mandarinkafe.mandarin.features.more.data.dto.AppStoreDto
import com.mandarinkafe.mandarin.features.more.domain.models.AppStore

fun AppStoreDto.toDomain(): AppStore {
    return AppStore(
        storeId = id,
        label = label,
        url = url,
    )
}
