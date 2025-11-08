package com.mandarinkafe.mandarin.core.di

import org.koin.core.Koin

object ServiceLocator {
    @Suppress("LateinitUsage")
    lateinit var koin: Koin
}