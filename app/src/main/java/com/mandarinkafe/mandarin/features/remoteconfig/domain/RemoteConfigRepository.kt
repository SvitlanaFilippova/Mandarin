package com.mandarinkafe.mandarin.features.remoteconfig.domain

import com.mandarinkafe.mandarin.features.remoteconfig.domain.model.FeatureToggle

interface RemoteConfigRepository {
    suspend fun fetchRemoteConfig(): Result<FeatureToggle>
}