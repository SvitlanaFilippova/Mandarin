package com.mandarinkafe.mandarin.core.data.network.impl

import com.mandarinkafe.mandarin.core.data.dto.CsvResponse
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsApiService
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsNetworkClient
import com.mandarinkafe.mandarin.util.AppLog
import com.mandarinkafe.mandarin.util.Constants.GOOGLE_DOCS_BASE_URL
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.NetworkMonitor

class GoogleDocsNetworkClientImpl(
    private val networkMonitor: NetworkMonitor,
    private val googleDocsApi: GoogleDocsApiService
) : GoogleDocsNetworkClient {

    private fun isConnected(): Boolean {
        return networkMonitor.isNetworkAvailable()
    }

    override suspend fun getBanners(): Response {
        return getSheet(BANNERS_GOOGLE_DOCS_URL)
    }

    override suspend fun getRecommendations(): Response {
        return getSheet(RECOMMENDATIONS_GOOGLE_DOCS_URL)
    }

    override suspend fun getDeliveryZonesPoints(): Response {
        return getSheet(DELIVERY_ZONES_POINTS_URL)
    }

    override suspend fun getDeliveryZonesMetaData(): Response {
        return getSheet(DELIVERY_ZONES_META_URL)
    }

    private suspend fun getSheet(url: String): Response {
        val finalUrl = GOOGLE_DOCS_BASE_URL + GOOGLE_SHEET_URL + url
        return if (!isConnected()) {
            Response().apply { resultCode = NO_CONNECTION }
        } else {
            try {
                val csvString = googleDocsApi.getCsv(finalUrl)
                return CsvResponse(csv = csvString).apply {
                    resultCode = HTTP_SUCCESS
                }
            } catch (e: Throwable) {
                AppLog.e("Не удалось прочитать csv. ${e.message}")
                Response().apply { resultCode = HTTP_SERVER_ERROR }
            }
        }
    }

    private companion object {
        const val GOOGLE_SHEET_URL =
            "2PACX-1vQ3-6HvveASGgkJk7RppqB25IlbRSGJGvdEnN_0_XTtIKtRcR6H-R4KS0L_39ifx1cnGWRUiCA2zPQZ/"
        const val BANNERS_GOOGLE_DOCS_URL =
            "pub?gid=0&single=true&output=csv"
        const val RECOMMENDATIONS_GOOGLE_DOCS_URL =
            "pub?gid=1629216186&single=true&output=csv"
        const val DELIVERY_ZONES_META_URL = "pub?gid=1299769815&single=true&output=csv"
        const val DELIVERY_ZONES_POINTS_URL = "pub?gid=994122877&single=true&output=csv"
    }
}