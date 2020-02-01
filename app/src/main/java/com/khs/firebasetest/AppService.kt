package com.khs.firebasetest

import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface AppService {

    @GET("/data/2.5/weather")
    fun getWeatherInfoOfLocation(
        @Query("q")location:String,
        @Query("APPID")appID:String
    ): Call<TotalWeather>

    @GET("/data/2.5/weather")
    fun getWeatherInfoOfCoordinates(
        @Query("lat") latitude:Double,
        @Query("lon") longitude:Double,
        @Query("APPID") appID:String,
        @Query("units") units:String,
        @Query("lang") language:String
    ): Call<TotalWeather>

}