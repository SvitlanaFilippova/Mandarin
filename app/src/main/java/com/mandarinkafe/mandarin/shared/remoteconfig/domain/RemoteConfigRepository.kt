package com.mandarinkafe.mandarin.shared.remoteconfig.domain

import com.mandarinkafe.mandarin.shared.remoteconfig.domain.model.FeatureToggle

interface RemoteConfigRepository {
    suspend fun fetchRemoteConfig(): Result<FeatureToggle>
}