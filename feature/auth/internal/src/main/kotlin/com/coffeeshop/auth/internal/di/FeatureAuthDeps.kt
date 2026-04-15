package com.coffeeshop.auth.internal.di

import android.content.Context
import com.coffeeshop.buildconfig.api.BuildConfigProvider
import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface FeatureAuthDeps {

    val context: Context

    val buildConfigProvider: BuildConfigProvider

    val client: OkHttpClient

    val retrofit: Retrofit

//    companion object {
//
//        fun create(): FeatureAuthDeps {
//            return DaggerFeatureAuthDeps.builder()
//                .build()
//        }
//    }
}