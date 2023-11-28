package com.hloriot.weatherapp.presentation.weather

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hloriot.weatherapp.R
import com.hloriot.weatherapp.data.OpenWeatherRepository
import com.hloriot.weatherapp.domain.City
import com.hloriot.weatherapp.domain.WeatherRepository
import com.hloriot.weatherapp.presentation.model.DispatchersProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class WeatherViewModel(
    private val context: Application,
    private val dispatchersProvider: DispatchersProvider = DispatchersProvider()
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherState())
    val uiState: StateFlow<WeatherState> = _uiState.asStateFlow()

    private val weatherRepository: WeatherRepository = OpenWeatherRepository()
    private var fetchCitiesDataJobs: List<Job> = emptyList()


    init {
        fetchCitiesData()
    }


    private fun fetchCitiesData() {
        _uiState.update { currentState ->
            currentState.copy(error = "")
        }
        fetchCitiesDataJobs = emptyList()

        // Repository
        fetchCitiesDataJobs += viewModelScope.launch(dispatchersProvider.io) {
            //com.google.code.gson:gson:2.10.1'
            _uiState.update { currentState ->
                currentState.copy(cities = emptyList())
            }

            val citiesFlow = flow {
                // List of cities can also be placed in domain
                for (name in arrayOf("Rennes", "Paris", "Nantes", "Bordeaux", "Lyon")) {
                    emit(name)
                    delay(10.seconds)
                }
            }.map { name ->
                val weather = weatherRepository.getWeather(City(name))

                CityState(
                    name,
                    weather.temperature,
                    weather.condition,
                    weather.cloudCoveragePercent
                )
            }

            try {
                citiesFlow.collect { cityState ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            cities = currentState.cities + cityState
                        )
                    }
                }
            } catch (_: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = context.getString(R.string.generic_error)
                    )
                }
                fetchCitiesDataJobs.forEach { it.cancel() }
            }
        }

        // Progress
        fetchCitiesDataJobs += viewModelScope.launch(dispatchersProvider.io) {
            //com.google.code.gson:gson:2.10.1'
            val progressFlow = flow {
                for (progress in 0..600) {
                    emit(progress)
                    delay(100.milliseconds)
                }
            }

            progressFlow.collect { progress ->
                _uiState.update { currentState ->
                    currentState.copy(loadingProgress = progress/600F)
                }
            }

            fetchCitiesDataJobs.forEach { it.cancel() }
        }

        // Message
        fetchCitiesDataJobs += viewModelScope.launch(dispatchersProvider.io) {
            //com.google.code.gson:gson:2.10.1'
            val messagesFlow = flow {
                val messages = context.resources.getStringArray(R.array.loading_messages)
                var currentIndex = 0

                while (true) {
                    emit(messages[currentIndex])
                    currentIndex = (currentIndex + 1) % messages.size
                    delay(6.seconds)
                }
            }

            messagesFlow.collect { message ->
                _uiState.update { currentState ->
                    currentState.copy(loadingMessage = message)
                }
            }
        }
    }

    fun onRestartClick() {
        fetchCitiesData()
    }
}