package com.coffeeshop.json

import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json

@Module
internal object JsonModule {

    @[Provides JsonScope]
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = true
        encodeDefaults = true
        decodeEnumsCaseInsensitive = true
    }
}