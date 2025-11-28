package com.mandarinkafe.mandarin.features.more.presentation.models

import com.mandarinkafe.mandarin.MR
import dev.icerock.moko.resources.ImageResource

private const val STORE_ID_RUSTORE = "rustore"
private const val STORE_ID_APPSTORE = "appstore"
private const val STORE_ID_PLAYSTORE = "playstore"
private const val STORE_ID_APPGALLERY = "appgallery"

fun getStoreIcon(storeId: String): ImageResource? {
    return when (storeId.lowercase()) {
        STORE_ID_RUSTORE -> MR.images.ic_rustore
        STORE_ID_APPSTORE -> MR.images.ic_appstore
        STORE_ID_PLAYSTORE -> MR.images.ic_playstore
        STORE_ID_APPGALLERY -> MR.images.ic_appgallery
        else -> null
    }
}
