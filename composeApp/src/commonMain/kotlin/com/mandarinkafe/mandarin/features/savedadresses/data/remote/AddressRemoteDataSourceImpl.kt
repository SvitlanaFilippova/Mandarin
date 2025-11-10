package com.mandarinkafe.mandarin.features.savedadresses.data.remote

import com.mandarinkafe.mandarin.core.domain.models.Address
import com.mandarinkafe.mandarin.features.auth.domain.api.AuthRepository
import com.mandarinkafe.mandarin.features.savedadresses.data.mapper.AddressMapper.toDomain
import com.mandarinkafe.mandarin.features.savedadresses.data.mapper.AddressMapper.toDto
import com.mandarinkafe.mandarin.features.savedadresses.data.network.AddressServerApi
import com.mandarinkafe.mandarin.features.savedadresses.data.network.AddressUpdateRequest
import io.github.aakira.napier.Napier

class AddressRemoteDataSourceImpl(
    private val api: AddressServerApi,
    private val authRepository: AuthRepository,
) : AddressRemoteDataSource {

    override suspend fun getAddresses(): List<Address> {
        val token = authRepository.getAccessToken() ?: return emptyList()
        return try {
            val response = api.getAddresses("Bearer $token")
            response.data?.map { addressDto ->
                addressDto.toDomain()
            } ?: emptyList()
        } catch (e: Exception) {
            Napier.e("AddressRemoteDataSource, getAddresses error: $e")
            emptyList()
        }
    }

    override suspend fun saveAddress(address: Address) {
        val token = authRepository.getAccessToken() ?: return
        try {
            val addressDto = address.toDto()
            val request = AddressUpdateRequest(data = addressDto)
            api.createOrUpdateAddress("Bearer $token", request)
        } catch (e: Exception) {
            Napier.e("AddressRemoteDataSource, saveAddress error: $e")
        }
    }

    override suspend fun removeAddress(id: String) {
        val token = authRepository.getAccessToken() ?: return
        try {
            api.deleteAddress("Bearer $token", id)
        } catch (e: Exception) {
            Napier.e("AddressRemoteDataSource, removeAddress error: $e")
        }
    }
}

