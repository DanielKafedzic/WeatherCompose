package com.example.weathercompose

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.weathercompose.api.NetworkClient
import com.google.android.gms.location.*
import com.google.android.gms.location.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.weathercompose.dto.WeatherTime


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
        binding.progressBar.visibility = View.VISIBLE

        client.getForecast(latitude, longitude).enqueue(object : Callback<WeatherTime> {
            override fun onResponse(call: Call<WeatherTime>, response: Response<WeatherTime>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    data = response.body()

                } else {
                    binding.label.text = getString(R.string.response_error, response.code(), response.errorBody())
                }
            }

            override fun onFailure(call: Call<WeatherTime>, t: Throwable) {
                binding.progressBar.visibility = View.GONE

                Toast.makeText(
                    this@MainActivity, t.localizedMessage, Toast.LENGTH_SHORT
                ).show()
                t.printStackTrace()
            }
        })
    }


    private fun requestPermissionLauncher() {
        { isGranted: Boolean ->
            if (isGranted) {
                requestLocationUpdates()
            } else {
                showPermissionRationaleDialog()
            }
        }
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        }
    }

    private fun showPermissionRationaleDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
        builder.setMessage("Niste dali dozvolu za lokaciju, ne moze aplikacija nastaviti.")
        builder.setTitle("Upozorenje")
        builder.setCancelable(false)
        builder.setPositiveButton("Ok") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            finish()
        }
        val alertDialog = builder.create()
        alertDialog.show()
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
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasicTextField(
                latitudeState.value,
                onValueChange = { latitudeState.value = it },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            BasicTextField(
                value = longitudeState.value,
                onValueChange = { longitudeState.value = it },
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