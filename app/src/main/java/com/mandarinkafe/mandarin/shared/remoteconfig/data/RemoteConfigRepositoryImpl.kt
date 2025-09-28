package com.mandarinkafe.mandarin.shared.remoteconfig.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.mandarinkafe.mandarin.shared.remoteconfig.domain.RemoteConfigRepository
import com.mandarinkafe.mandarin.shared.remoteconfig.domain.model.FeatureToggle
import com.mandarinkafe.mandarin.util.Constants.PHONE_NUMBER_DEFAULT
import com.mandarinkafe.mandarin.util.Constants.PHONE_NUMBER_KEY
import kotlinx.coroutines.tasks.await

class RemoteConfigRepositoryImpl(
    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
) : RemoteConfigRepository {

    init {
        val configSettings = remoteConfigSettings {
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        val defaults = mapOf(
            PHONE_NUMBER_KEY to PHONE_NUMBER_DEFAULT,
        )
        remoteConfig.setDefaultsAsync(defaults)
    }

    override suspend fun fetchRemoteConfig(): Result<FeatureToggle> = try {
        remoteConfig.fetchAndActivate().await()

        val feature = FeatureToggle(
            phoneNumber = remoteConfig.getString(PHONE_NUMBER_KEY),
        )

        Result.success(feature)

    } catch (e: Exception) {
        Result.failure(e)
    }
}