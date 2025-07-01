package com.mandarinkafe.mandarin.core.data.network

import android.util.Log
import com.mandarinkafe.mandarin.BuildConfig
import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.dto.CsvResponse
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.menu.data.network.MenuRequest
import com.mandarinkafe.mandarin.util.Constants.BANNERS_GOOGLE_DOCS_URL
import com.mandarinkafe.mandarin.util.Constants.BEARER_PREFIX
import com.mandarinkafe.mandarin.util.Constants.GOOGLE_DOCS_BASE_URL
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.Constants.NO_CONNECTION
import com.mandarinkafe.mandarin.util.Constants.RECOMMENDATIONS_GOOGLE_DOCS_URL
import com.mandarinkafe.mandarin.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val networkMonitor: NetworkMonitor,
    private val iikoService: IikoApiService,
    private val googleDocsApi: GoogleDocsApiService
) :
    NetworkClient {

    private var token = ""
    private var organizationId = ""
    private var externalMenuId = ""

    override suspend fun getMenu(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_CONNECTION }
        }
        return withContext(Dispatchers.IO) {
            ensureAuthenticated()
            fetchMenu()
        }
    }

    private suspend fun ensureAuthenticated() {
        if (token.isEmpty() || organizationId.isEmpty()) authenticate()
    }

    private suspend fun fetchMenu(): Response {
        return try {
            if (externalMenuId.isEmpty()) {
                externalMenuId = getExternalMenuId()
            }
            val menuResponse = iikoService.getMenuById(
                token = token,
                body = MenuRequest(
                    externalMenuId = externalMenuId,
                    organizationIds = listOf(organizationId)
                )
            )
            menuResponse.apply { resultCode = HTTP_SUCCESS }
        } catch (e: Throwable) {
            Log.d("DEBUG IIKO API", "Ошибка: ${e.message}")
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    private suspend fun getExternalMenuId(): String {
        val menuIdResponse = iikoService.getMenuId(token)
        return menuIdResponse.externalMenus.firstOrNull()?.id
            ?: error("Menu ID not found")
    }

    private suspend fun authenticate() {
        if (token.isNotEmpty() && organizationId.isNotEmpty()) {
            // Уже авторизованы
            return
        }

        try {
            val authResponse = iikoService.authenticate(AuthRequest(BuildConfig.IIKO_API_KEY))
            token = BEARER_PREFIX + authResponse.token

            val organizationsResponse = iikoService.getOrganizations(
                token = token,
                body = OrganizationsRequest()
            )
            organizationId = organizationsResponse.organizations.firstOrNull()?.id
                ?: error("No organization found")
        } catch (e: Throwable) {
            Log.d("DEBUG IIKO API", "Ошибка в методе authenticate: ${e.message}")
        }
    }

    private fun isConnected(): Boolean {
        return networkMonitor.isNetworkAvailable()
    }

    private suspend fun getSheet(url: String): Response {
        val finalUrl = GOOGLE_DOCS_BASE_URL + url
        return if (!isConnected()) {
            Response().apply { resultCode = -1 }
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

    override suspend fun getBanners(): Response {
        return getSheet(BANNERS_GOOGLE_DOCS_URL)
    }

    override suspend fun getRecommendations(): Response {
        return getSheet(RECOMMENDATIONS_GOOGLE_DOCS_URL)
    }
}