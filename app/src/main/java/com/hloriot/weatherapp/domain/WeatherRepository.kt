package com.hloriot.weatherapp.domain

interface WeatherRepository {
    suspend fun getWeather(city: City): Weather
}