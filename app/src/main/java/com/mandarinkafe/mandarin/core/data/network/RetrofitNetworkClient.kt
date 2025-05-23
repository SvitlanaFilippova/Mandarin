package com.mandarinkafe.mandarin.core.data.network

import android.content.Context
import android.util.Log
import com.mandarinkafe.mandarin.BuildConfig
import com.mandarinkafe.mandarin.core.data.dto.AuthRequest
import com.mandarinkafe.mandarin.core.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.core.data.dto.Response
import com.mandarinkafe.mandarin.features.menu.data.network.MenuRequest
import com.mandarinkafe.mandarin.util.Constants.BEARER_PREFIX
import com.mandarinkafe.mandarin.util.Constants.HTTP_SERVER_ERROR
import com.mandarinkafe.mandarin.util.Constants.HTTP_SUCCESS
import com.mandarinkafe.mandarin.util.NetworkMonitor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val context: Context, private val iikoService: IikoApiService) :
    NetworkClient {

    private var token = ""
    private var organizationId = ""
    private var externalMenuId = ""

    override suspend fun getMenu(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }
        return withContext(Dispatchers.IO) {
            // если нет токена или Id организации - авторизуемся
            if (token.isEmpty() || organizationId.isEmpty()) {
                authenticate()
            }
            try {
                // если нет externalMenuId - запрашиваем
                if (externalMenuId.isEmpty()) {
                    val menuIdResponse = iikoService.getMenuId(token)
                    externalMenuId = menuIdResponse.externalMenus.firstOrNull()?.id
                        ?: throw IllegalStateException("Menu ID not found")
                    Log.d("DEBUG IIKO API", "Menu ID получено: $externalMenuId")
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
                ?: throw IllegalStateException("No organization found")
        } catch (e: Throwable) {
            Log.d("DEBUG IIKO API", "Ошибка в методе authenticate: ${e.message}")
            Response().apply { resultCode = HTTP_SERVER_ERROR }
        }
    }

    private fun isConnected(): Boolean {
        return NetworkMonitor.isNetworkAvailable(context)
    }
}