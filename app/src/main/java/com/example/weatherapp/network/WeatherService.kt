package com.example.weatherapp.network

import com.example.weatherapp.models.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface WeatherService {
    //v2/informers
    @GET("v1/forecast")
    fun getWeather(@Query("lat") lat: Double,
                   @Query("lon") lon: Double,
                   @Query("extra") extra: Boolean,
                   @Header("X-Yandex-API-Key") appid: String?
                   ): Call<WeatherResponse>
}