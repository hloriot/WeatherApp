package com.hloriot.weatherapp.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hloriot.weatherapp.R
import com.hloriot.weatherapp.presentation.theme.WeatherAppTheme

@Composable
fun Home(
    onStartPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .padding(8.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Spacer(modifier = Modifier.weight(1F))
        Image(
            modifier = Modifier.size(256.dp),
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(
                id = R.string.app_name
            )
        )
        Text(
            text = stringResource(id = R.string.welcome),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 21.sp
        )
        Spacer(modifier = Modifier.weight(1F))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onStartPressed,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = stringResource(id = R.string.start))
        }
    }
}

@Preview
@Composable
fun HomePreview() {
    WeatherAppTheme {
        Surface {
            Home(onStartPressed = { })
        }
    }
}