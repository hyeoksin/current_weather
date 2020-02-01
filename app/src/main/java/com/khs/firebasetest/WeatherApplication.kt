package com.khs.firebasetest

import android.app.Application
import android.util.Log
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T

class WeatherApplication : Application(){

    var appService:AppService? =null

    override fun onCreate() {
        Log.d("DEBUG","WeatherApplication() onCreate()")
        super.onCreate()
        Fabric.with(this, Crashlytics())
        Stetho.initializeWithDefaults(this)
        setupRetrofit()
    }

    private fun setupRetrofit(){

        val httpClient = OkHttpClient.Builder()
        httpClient.addNetworkInterceptor(StethoInterceptor())
        val client = httpClient.build()

        var retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        appService = retrofit.create(AppService::class.java)
    }

    fun requestAppService():AppService? {
        return appService
    }
}