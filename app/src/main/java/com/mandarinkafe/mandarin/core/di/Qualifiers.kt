package com.mandarinkafe.mandarin.core.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IikoClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ServerClient


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GoogleDocsClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TelegramClient