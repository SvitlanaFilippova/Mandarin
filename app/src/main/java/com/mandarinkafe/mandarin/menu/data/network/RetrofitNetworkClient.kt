package com.mandarinkafe.mandarin.menu.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.mandarinkafe.mandarin.menu.data.dto.AuthRequest
import com.mandarinkafe.mandarin.menu.data.dto.MenuRequest
import com.mandarinkafe.mandarin.menu.data.dto.OrganizationsRequest
import com.mandarinkafe.mandarin.menu.data.dto.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class RetrofitNetworkClient(private val context: Context, private val ikkoService: IkkoApiService) :
    NetworkClient {

    private var token = ""
    private var organizationId = ""
    private var externalMenuId = ""


    override suspend fun doRequest(): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }
        return withContext(Dispatchers.IO) {
            try {
                val authResponse = ikkoService.authenticate(AuthRequest(API_LOGIN))
                token =
                    "Bearer ${authResponse.token}"
                Log.d("DEBUG IKKO API", "Токен получен: $token")

                val organizationsResponse = ikkoService.getOrganizations(
                    token = token,
                    body = OrganizationsRequest()
                )

                organizationId = organizationsResponse.organizations.firstOrNull()?.id
                    ?: throw IllegalStateException("No organization found")
                Log.d("DEBUG IKKO API", "Организация получена: $organizationId")

                val menuIdResponse = ikkoService.getMenuId(token)
                externalMenuId = menuIdResponse.externalMenus.firstOrNull()?.id
                    ?: throw IllegalStateException("Menu ID not found")
                Log.d("DEBUG IKKO API", "Menu ID получено: $externalMenuId")


                val menuResponse = ikkoService.getMenuById(
                    token = token,
                    body = MenuRequest(
                        externalMenuId = externalMenuId,
                        organizationIds = listOf(organizationId)
                    )
                )
                Log.d("DEBUG IKKO API", "Данные меню получены: ${menuResponse.itemCategories}")
                menuResponse.apply { resultCode = 200 }


            } catch (e: Throwable) {
                Log.d("DEBUG IKKO API", "Ошибка: ${e.message}")
                Response().apply { resultCode = 500 }
            }
        }
    }


    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
    companion object {
        const val API_LOGIN = "3a901a233be740bea54bf0a38e1bfaa3"
    }

}
