package com.mandarinkafe.mandarin.features.menu.di

import com.mandarinkafe.mandarin.features.menu.data.api.ImageValidator
import com.mandarinkafe.mandarin.features.menu.data.impl.ImageValidatorImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val menuPlatformModule = module {
    // ImageValidator (Android-specific)
    singleOf(::ImageValidatorImpl) { bind<ImageValidator>() }

}

