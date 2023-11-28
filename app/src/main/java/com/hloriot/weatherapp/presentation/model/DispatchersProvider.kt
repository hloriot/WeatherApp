package com.hloriot.weatherapp.presentation.model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

data class DispatchersProvider(
    val ui: CoroutineDispatcher = Dispatchers.Main,
    val io: CoroutineDispatcher =  Dispatchers.IO,
    val background: CoroutineDispatcher = Dispatchers.Default
)