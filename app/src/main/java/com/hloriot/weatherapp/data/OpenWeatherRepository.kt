package com.hloriot.weatherapp.data

import com.hloriot.weatherapp.domain.City
import com.hloriot.weatherapp.domain.Weather
import com.hloriot.weatherapp.domain.WeatherCondition
import com.hloriot.weatherapp.domain.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class OpenWeatherRepository(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : WeatherRepository {

    private fun mapWeatherId(weatherId: Int): WeatherCondition {
        return when (weatherId) {
            in 200..299 -> WeatherCondition.Thunderstorm
            in 300..399 -> WeatherCondition.Drizzle
            in 500..599 -> WeatherCondition.Rain
            in 600..699 -> WeatherCondition.Snow
            in 700..799 -> WeatherCondition.Atmosphere
            800 -> WeatherCondition.Clear
            in 801..809 -> WeatherCondition.Clouds
            else -> WeatherCondition.Clear
        }
    }

    override suspend fun getWeather(city: City): Weather = withContext(ioDispatcher) {
        // TODO: Fallback to q=CityName if city.id == 0
        val url =
            URL("https://api.openweathermap.org/data/2.5/weather?id=${city.id}&appid=$API_KEY&units=metric")

        val json = JSONObject(
            url.openConnection()
                .apply { connect() }
                .getInputStream()
                .bufferedReader()
                .use { it.readText() }
        )

        val temperature = json.getJSONObject("main").getDouble("temp").toFloat()
        val weatherCondition = mapWeatherId(json.getJSONArray("weather").getJSONObject(0).getInt("id"))
        val cloudCoverage = json.getJSONObject("clouds").getInt("all")

        Weather(temperature, weatherCondition, cloudCoverage)
    }

    private companion object {
        private const val API_KEY = "d93fcd97823b0d001254cfab7e70e06e"
    }

}

// Use ID as recommended by the documentation
private val City.id: Int
    get() = when (name) {
        "Rennes" -> 2983990
        "Paris" -> 2968815
        "Nantes" -> 2990969
        "Bordeaux" -> 3031582
        "Lyon" -> 6454573
        else -> 0
    }