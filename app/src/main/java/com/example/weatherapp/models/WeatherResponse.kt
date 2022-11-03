package com.example.weatherapp.models

import android.net.Uri
import com.google.gson.JsonElement
import java.io.Serializable

data class WeatherResponse(
    val now: Long,
    val now_dt: String?,
    val info: Info,
    val geo_object: GeoObject,
    val yesterday: Yesterday,
    val fact: Fact,
    val forecasts: List<Forecast>
): Serializable

data class Yesterday(
    val temp: Int
): Serializable

data class Info (
    val n: Boolean,
    val geoid: Double,
    val url: String?,
    val lat: Double,
    val lon: Double,
    val tzinfo: Tzinfo,
    val def_pressure_mm: Double,
    val def_pressure_pa: Double,
    val slug: String?,
    val zoom: Double,
    val nr: Boolean,
    val ns: Boolean,
    val nsr: Boolean,
    val p: Boolean,
    val f: Boolean,
    val _h: Boolean,
): Serializable

data class Tzinfo(
    val name: String?,
    val abbr: String?,
    val dst: Boolean,
    val offset: Double
): Serializable

data class GeoObject(
    var district: District?,
    val locality: Locality,
    val province: Province,
    val country: Country
): Serializable

data class  District(
    val id: Double,
    val name: String?
): Serializable


data class Locality(
    val id: Double,
    val name: String?
): Serializable

data class Province(
    val id: Double,
    val name: String?
): Serializable

data class Country(
    val id: Double,
    val name: String?
): Serializable

data class Fact(
    val obs_time: Long,
    val uptime: Long,
    val temp: Double,
    val feels_like: Double,
    val icon: String?,
    val condition: String?,
    val cloudness: Double,
    val prec_type: Double,
    val prec_prob: Double,
    val prec_strength: Double,
    val is_thunder: Boolean,
    val wind_speed: Double,
    val wind_dir: String?,
    val pressure_mm: Double,
    val pressure_pa: Double,
    val humidity: Double,
    val daytime: String?,
    val polar: Boolean,
    val season: String?,
    val source: String?,
    val accumPrec: AccumPrec,
    val soil_moisture: Double,
    val soil_temp: Double,
    val uv_index: Double,
    val wind_gust: Double

): Serializable

data class AccumPrec(
    val one: Double,
    val three: Double,
    val five: Double
): Serializable

data class Forecast(
    val date: String?,
    val date_ts: Long,
    val week: Double,
    val sunrise: String?,
    val sunset: String?,
    val rise_begin: String?,
    val set_end: String?,
    val moon_code: Double,
    val moon_text: String?,
    val parts: Parts,
    val hours: List<Hour>,
    val biomet: Biomet
): Serializable

data class Parts(
    val morning: Morning,
    val DayShort: DayShort,
    val day: Day,
    val evening: Evening,
    val night: Night,
    val NightShort: NightShort
): Serializable

data class Morning(
    val _source: String?,
    val temp_min: Double,
    val temp_avg: Double,
    val temp_max: Double,
    val wind_speed: Double,
    val wind_gust: Double,
    val wind_dir: String?,
    val pressure_mm: Double,
    val pressure_pa: Double,
    val humidity: Double,
    val soil_temp: Double,
    val soil_moisture: Double,
    val prec_mm: Double,
    val prec_prob: Double,
    val prec_period: Double,
    val cloudness: Double,
    val prec_type: Double,
    val prec_strength: Double,
    val icon: String?,
    val condition: String?,
    val uv_index: Double,
    val feels_like: Double,
    val daytime: String?,
    val polar: Boolean
): Serializable
data class DayShort(
    val _source: String?,
    val temp_min: Double,
    val temp_avg: Double,
    val temp_max: Double,
    val wind_speed: Double,
    val wind_gust: Double,
    val wind_dir: String?,
    val pressure_mm: Double,
    val pressure_pa: Double,
    val humidity: Double,
    val soil_temp: Double,
    val soil_moisture: Double,
    val prec_mm: Double,
    val prec_prob: Double,
    val prec_period: Double,
    val cloudness: Double,
    val prec_type: Double,
    val prec_strength: Double,
    val icon: String?,
    val condition: String?,
    val uv_index: Double,
    val feels_like: Double,
    val daytime: String?,
    val polar: Boolean
): Serializable
data class Day(
    val _source: String?,
    val temp_min: Double,
    val temp_avg: Double,
    val temp_max: Double,
    val wind_speed: Double,
    val wind_gust: Double,
    val wind_dir: String?,
    val pressure_mm: Double,
    val pressure_pa: Double,
    val humidity: Double,
    val soil_temp: Double,
    val soil_moisture: Double,
    val prec_mm: Double,
    val prec_prob: Double,
    val prec_period: Double,
    val cloudness: Double,
    val prec_type: Double,
    val prec_strength: Double,
    val icon: String?,
    val condition: String?,
    val uv_index: Double,
    val feels_like: Double,
    val daytime: String?,
    val polar: Boolean
): Serializable
data class Evening(
    val _source: String?,
    val temp_min: Double,
    val temp_avg: Double,
    val temp_max: Double,
    val wind_speed: Double,
    val wind_gust: Double,
    val wind_dir: String?,
    val pressure_mm: Double,
    val pressure_pa: Double,
    val humidity: Double,
    val soil_temp: Double,
    val soil_moisture: Double,
    val prec_mm: Double,
    val prec_prob: Double,
    val prec_period: Double,
    val cloudness: Double,
    val prec_type: Double,
    val prec_strength: Double,
    val icon: String?,
    val condition: String?,
    val uv_index: Double,
    val feels_like: Double,
    val daytime: String?,
    val polar: Boolean
): Serializable
data class Night(
    val _source: String?,
    val temp_min: Double,
    val temp_avg: Double,
    val temp_max: Double,
    val wind_speed: Double,
    val wind_gust: Double,
    val wind_dir: String?,
    val pressure_mm: Double,
    val pressure_pa: Double,
    val humidity: Double,
    val soil_temp: Double,
    val soil_moisture: Double,
    val prec_mm: Double,
    val prec_prob: Double,
    val prec_period: Double,
    val cloudness: Double,
    val prec_type: Double,
    val prec_strength: Double,
    val icon: String?,
    val condition: String?,
    val uv_index: Double,
    val feels_like: Double,
    val daytime: String?,
    val polar: Boolean
): Serializable
data class NightShort(
    val _source: String?,
    val temp_min: Double,
    val temp_avg: Double,
    val temp_max: Double,
    val wind_speed: Double,
    val wind_gust: Double,
    val wind_dir: String?,
    val pressure_mm: Double,
    val pressure_pa: Double,
    val humidity: Double,
    val soil_temp: Double,
    val soil_moisture: Double,
    val prec_mm: Double,
    val prec_prob: Double,
    val prec_period: Double,
    val cloudness: Double,
    val prec_type: Double,
    val prec_strength: Double,
    val icon: String?,
    val condition: String?,
    val uv_index: Double,
    val feels_like: Double,
    val daytime: String?,
    val polar: Boolean
): Serializable

data class Hour(
    val hour: String?,
    val hour_ts: Long,
    val temp: Double,
    val feels_like: Double,
    val icon: String?,
    val condition: String?,
    val cloudness: Double,
    val prec_type: Double,
    val prec_strength: Double,
    val is_thunder: Boolean,
    val wind_dir: String?,
    val wind_speed: Double,
    val wind_gust: Double,
    val pressure_pa: Double,
    val humidity: Double,
    val uv_index: Double,
    val soil_temp: Double,
    val soil_moisture: Double,
    val prec_mm: Double,
    val prec_period: Double,
    val prec_prob: Double
): Serializable

data class Biomet(
    val index: Double,
    val condition: String?
): Serializable