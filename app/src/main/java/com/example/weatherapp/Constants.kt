package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.android.gms.location.FusedLocationProviderClient

object Constants {

    const val APP_ID: String = "c508ada2-1cf3-4f48-9d2f-8271fdf586ac"
    const val BASE_URL: String = "https://api.weather.yandex.ru/"
    const val METRIC_UNIT: String = "metric"
    const val LANG: String = "ru_RU"
    const val PREFERENCE_NAME = "WeatherAppPreference"
    const val WEATHER_RESPONSE_DATA = "WeatherResponseData"
    const val CURRENT_DAY = 0
    const val NEXT_DAY = 1
    const val PREVIOUS_DAY = -1
    const val NIGHT_HOUR = 4
    const val DAY_HOUR = 16

    val conditions = mapOf("clear" to "ясно",
    "partly-cloudy" to "малооблачно",
    "cloudy" to "облачно с прояснениями",
    "overcast" to "пасмурно",
    "drizzle" to "морось",
    "light-rain" to "небольшой дождь",
    "rain" to "дождь",
    "moderate-rain" to "умеренно сильный дождь",
    "heavy-rain" to "сильный дождь",
    "continuous-heavy-rain" to "длительный сильный дождь",
    "showers" to "ливень",
    "wet-snow" to "дождь со снегом",
    "light-snow" to "небольшой снег",
    "snow" to "снег",
    "snow-showers" to "снегопад",
    "hail" to "град",
    "thunderstorm" to "гроза",
    "thunderstorm-with-rain" to "дождь с грозой",
    "thunderstorm-with-hail" to "гроза с градом")



    fun isNetWorkAvailable(context: Context): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when{
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->  true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->  true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->  true
                else ->  false
            }
        }else{
            val networkInfo = connectivityManager.activeNetworkInfo
            return  networkInfo != null && networkInfo.isConnectedOrConnecting
        }


    }
}