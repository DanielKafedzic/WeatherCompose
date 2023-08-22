package com.example.weathercompose

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.weathercompose.api.NetworkClient
import com.example.weathercompose.WeatherAppTheme


class MainActivity : ComponentActivity() {
    private val client = NetworkClient()

    private val locationRequest = LocationRequest.create().apply {
        interval = 60000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            requestLocationUpdates()
        } else {
            showPermissionRationaleDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                WeatherApp()
            }
        }
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        // Implement your weather data fetching logic here
    }

    private fun requestPermissionLauncher() {
        // Implement permission launcher logic here
    }

    private fun requestLocationUpdates() {
        // Implement location updates logic here
    }

    private fun showPermissionRationaleDialog() {
        // Implement permission rationale dialog logic here
    }
}

@Composable
fun WeatherApp() {
    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)
    val latitudeState = remember { mutableStateOf("") }
    val longitudeState = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Weather App") })
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasicTextField(
                value = latitudeState.value,
                onValueChange = { latitudeState.value = it },
                label = { Text("Latitude") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            BasicTextField(
                value = longitudeState.value,
                onValueChange = { longitudeState.value = it },
                label = { Text("Longitude") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Button(
                onClick = {
                    val latitude = latitudeState.value.toDoubleOrNull()
                    val longitude = longitudeState.value.toDoubleOrNull()

                    if (latitude != null && longitude != null) {
                        fetchWeatherData(latitude, longitude)
                    }
                }
            ) {
                Text("Fetch Weather")
            }
        }
    }
}
