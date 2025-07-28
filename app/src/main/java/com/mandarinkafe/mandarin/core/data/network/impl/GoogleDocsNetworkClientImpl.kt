package com.mandarinkafe.mandarin.core.data.network.impl

import android.util.Log
import com.mandarinkafe.mandarin.core.data.dto.CsvResponse
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsApiService
import com.mandarinkafe.mandarin.core.data.network.GoogleDocsNetworkClient
import com.mandarinkafe.mandarin.util.Constants.BANNERS_GOOGLE_DOCS_URL
import com.mandarinkafe.mandarin.util.Constants.GOOGLE_DOCS_BASE_URL
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Constants.RECOMMENDATIONS_GOOGLE_DOCS_URL
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

    private suspend fun getSheet(url: String): Response {
        val finalUrl = GOOGLE_DOCS_BASE_URL + url
        return if (!isConnected()) {
            Response().apply { resultCode = NO_CONNECTION }
        } else {
            try {
                val csvString = googleDocsApi.getCsv(finalUrl)
                return CsvResponse(csv = csvString)

            } catch (e: Throwable) {
                Log.d("getSheet", "Не удалось прочитать csv. ${e.message}")
                Response().apply { resultCode = HTTP_SERVER_ERROR }
            }
        }
    }
}