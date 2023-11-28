package com.hloriot.weatherapp.presentation.weather

data class WeatherState(
    val loadingProgress: Float = 0F,
    val loadingMessage: String = "",
    val cities: List<CityState> = emptyList(),
    val error: String = ""
)
