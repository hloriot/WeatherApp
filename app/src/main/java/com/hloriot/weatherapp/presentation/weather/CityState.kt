package com.hloriot.weatherapp.presentation.weather

import com.hloriot.weatherapp.domain.WeatherCondition

data class CityState(
    val name: String,
    val temperature: Float,
    val condition: WeatherCondition,
    val cloudCoveragePercent: Int
)
