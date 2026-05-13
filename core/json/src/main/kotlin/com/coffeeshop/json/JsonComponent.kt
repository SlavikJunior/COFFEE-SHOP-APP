package com.coffeeshop.json

import dagger.Component
import kotlinx.serialization.json.Json

@Component(modules = [JsonModule::class])
@JsonScope
interface JsonComponent {

    fun json(): Json

    companion object {
        val get: JsonComponent by lazy { DaggerJsonComponent.create() }
    }
}