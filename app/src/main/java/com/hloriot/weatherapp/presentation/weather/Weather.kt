package com.hloriot.weatherapp.presentation.weather

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hloriot.weatherapp.R
import com.hloriot.weatherapp.domain.WeatherCondition
import com.hloriot.weatherapp.presentation.theme.WeatherAppTheme

@Composable
private fun GradiantLinearProgress(progress: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(ProgressIndicatorDefaults.linearTrackColor)
            .size(240.dp, 4.dp)
    ) {
        Box(
            modifier = modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(
                                red = (MaterialTheme.colorScheme.primary.red + ProgressIndicatorDefaults.linearTrackColor.red) / 2F,
                                green = (MaterialTheme.colorScheme.primary.green + ProgressIndicatorDefaults.linearTrackColor.green) / 2F,
                                blue = (MaterialTheme.colorScheme.primary.blue + ProgressIndicatorDefaults.linearTrackColor.blue) / 2F,
                            ),
                            MaterialTheme.colorScheme.primary
                        )
                    )
                )
                .size(240.dp * progress, 4.dp)
        )
    }
}

@Composable
private fun CloudCoverage(progress: Float, modifier: Modifier = Modifier) {
    val text = "%d %%".format((progress * 100).toInt())

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(id = R.drawable.cloud),
                contentDescription = stringResource(R.string.cloud),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
            )
            Box(
                modifier = Modifier
                    .background(ProgressIndicatorDefaults.linearTrackColor)
                    .size(4.dp, 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary)
                        .size(4.dp, 32.dp * progress)
                        .align(Alignment.BottomCenter)
                )
            }
        }
        Text(text = text, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
private fun ProgressBarWithText(progress: Float, modifier: Modifier = Modifier) {
    val text = "%d %%".format((progress * 100).toInt())

    Box(modifier = modifier.height(IntrinsicSize.Min), contentAlignment = Alignment.CenterEnd) {
        GradiantLinearProgress(
            modifier = Modifier
                .fillMaxHeight(),
            progress = progress
        )

        // TODO: Make text more visible
        Text(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = TextStyle(
                shadow = Shadow(
                    color = MaterialTheme.colorScheme.onPrimary,
                    offset = Offset(0F, 0F),
                    blurRadius = 3f
                )
            )
        )
    }
}

@Composable
private fun City(cityState: CityState, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = cityState.name, fontSize = 24.sp)
                Text(text = "%.2f°C".format(cityState.temperature), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.weight(1F))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    modifier = Modifier.size(32.dp),
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(weatherConditionToPictureUrl(cityState.condition))
                        .build(),
                    contentDescription = cityState.condition.name
                )

                CloudCoverage(progress = cityState.cloudCoveragePercent / 100F)
            }
        }
    }
}

@Composable
@VisibleForTesting
fun Weather(
    state: WeatherState,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {

        if (state.loadingProgress == 1F) {
            LazyVerticalGrid(
                //columns = GridCells.Adaptive(minSize = 128.dp),
                // As required by the specs, only 1 item by line but looks better with a grid
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.cities) { city ->
                    City(cityState = city)
                }

            }
        }

        Spacer(modifier = Modifier.weight(1F))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.error.isNotEmpty()) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
            }

            if (state.loadingProgress == 1F || state.error.isNotEmpty()) {
                Button(onClick = onRestartClick) {
                    Text(text = stringResource(id = R.string.restart))
                }
            } else {
                Crossfade(
                    targetState = state.loadingMessage,
                    label = "LoadingMessage"
                ) { loadingMessage ->
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = loadingMessage,
                        textAlign = TextAlign.Center
                    )
                }
                ProgressBarWithText(progress = state.loadingProgress)
            }
        }
    }
}

@Composable
fun Weather(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                WeatherViewModel(
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                )
            }
        }
    )
) {
    val state by viewModel.uiState.collectAsState()

    Weather(
        modifier = modifier,
        state = state,
        onRestartClick = viewModel::onRestartClick
    )
}

private fun weatherConditionToPictureUrl(weatherCondition: WeatherCondition): String {
    return when (weatherCondition) {
        WeatherCondition.Thunderstorm -> "https://openweathermap.org/img/wn/11d@2x.png"
        WeatherCondition.Drizzle -> "https://openweathermap.org/img/wn/09d@2x.png"
        WeatherCondition.Rain -> "https://openweathermap.org/img/wn/10d@2x.png"
        WeatherCondition.Snow -> "https://openweathermap.org/img/wn/13d@2x.png"
        WeatherCondition.Atmosphere -> "https://openweathermap.org/img/wn/50d@2x.png"
        WeatherCondition.Clear -> "https://openweathermap.org/img/wn/01d@2x.png"
        WeatherCondition.Clouds -> "https://openweathermap.org/img/wn/03d@2x.png"
    }
}

@Preview
@Composable
fun WeatherPreviewLoading() {
    WeatherAppTheme {
        Surface {
            Weather(
                state = WeatherState(
                    loadingProgress = 0.30F,
                    loadingMessage = stringArrayResource(id = R.array.loading_messages).first(),
                    cities = listOf(
                        CityState("Paris", 42.35F, WeatherCondition.Clear, 0)
                    )
                ),
                onRestartClick = {}
            )
        }
    }
}

@Preview
@Composable
fun WeatherPreviewCompleted() {
    WeatherAppTheme {
        Surface {
            Weather(
                state = WeatherState(
                    loadingProgress = 1F,
                    cities = listOf(
                        CityState("Rennes", 24.47F, WeatherCondition.Thunderstorm, 75),
                        CityState("Paris", 12.35F, WeatherCondition.Drizzle, 12),
                        CityState("Nantes", 17.49F, WeatherCondition.Rain, 50),
                        CityState("Bordeaux", 22.87F, WeatherCondition.Clouds, 100),
                        CityState("Lyon", 33.74F, WeatherCondition.Clear, 0)
                    )
                ),
                onRestartClick = {}
            )
        }
    }
}

@Preview
@Composable
fun WeatherPreviewError() {
    WeatherAppTheme {
        Surface {
            Weather(
                state = WeatherState(
                    error = stringResource(id = R.string.generic_error)
                ),
                onRestartClick = {}
            )
        }
    }
}
