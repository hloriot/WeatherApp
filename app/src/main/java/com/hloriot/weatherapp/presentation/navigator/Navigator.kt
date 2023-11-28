package com.hloriot.weatherapp.presentation.navigator

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hloriot.weatherapp.R
import com.hloriot.weatherapp.presentation.home.Home
import com.hloriot.weatherapp.presentation.navigator.Navigator.HOME_ROUTE
import com.hloriot.weatherapp.presentation.navigator.Navigator.WEATHER_ROUTE
import com.hloriot.weatherapp.presentation.weather.Weather

private object Navigator {
    const val HOME_ROUTE = "home"
    const val WEATHER_ROUTE = "weather"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigator(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var showBackArrow by remember { mutableStateOf(false) }

    navController.addOnDestinationChangedListener { _, _, _ ->
        showBackArrow = navController.previousBackStackEntry != null
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                navigationIcon = {
                    if (showBackArrow) {
                        IconButton(onClick = navController::navigateUp) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = HOME_ROUTE) {
            composable(HOME_ROUTE) {
                Home(
                    modifier = Modifier.padding(innerPadding),
                    onStartPressed = { navController.navigate(WEATHER_ROUTE) }
                )
            }

            composable(WEATHER_ROUTE) {
                Weather(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}