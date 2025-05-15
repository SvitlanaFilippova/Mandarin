package com.mandarinkafe.mandarin.remoteconfig.domain

import com.mandarinkafe.mandarin.remoteconfig.domain.model.FeatureToggle

interface RemoteConfigRepository {
    suspend fun fetchRemoteConfig(): Result<FeatureToggle>
}