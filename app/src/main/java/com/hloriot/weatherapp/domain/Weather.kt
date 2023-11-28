package com.hloriot.weatherapp.domain

data class Weather(
    val temperature: Float,
    val condition: WeatherCondition,
    val cloudCoveragePercent: Int
)