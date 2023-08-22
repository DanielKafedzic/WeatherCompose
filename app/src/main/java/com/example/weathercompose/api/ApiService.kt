package com.example.weathercompose.api

import com.example.weathercompose.dto.WeatherTime
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/v1/forecast")
    fun getForecast(@Query("latitude")latitude:String,
                    @Query("longitude")longitude:String,
                    @Query("current_weather")currentWeather:String,
                    @Query("hourly")hourly:String): Call<WeatherTime>
}