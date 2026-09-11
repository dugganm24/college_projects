package com.example.weatherapp.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.R
import com.example.weatherapp.data.local.SavedWeather
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.location.LocationProvider
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.history.HistoryActivity
import com.example.weatherapp.utils.Resource
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var locationProvider: LocationProvider
    private lateinit var locationTextView: TextView
    private lateinit var weatherTextView: TextView
    private lateinit var historyButton: Button
    private lateinit var logWeatherButton: Button


    private val viewModel: MainViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val database = WeatherDatabase.getDatabase(applicationContext)
                val repo = WeatherRepository(LocationProvider(applicationContext), database.savedWeatherDao())
                return MainViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logWeatherButton = findViewById(R.id.logWeatherButton)

        logWeatherButton.setOnClickListener {
            saveWeatherToDatabase()
        }

        locationTextView = findViewById(R.id.locationTextView)
        weatherTextView = findViewById(R.id.weatherTextView)
        historyButton = findViewById(R.id.historyButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        locationProvider = LocationProvider(this)

        viewModel.location.observe(this) { location ->
            if (location != null) {
                fetchAddressFromCoordinates(location)
                viewModel.fetchWeather(API_KEY)
            } else {
                locationTextView.text = "Location not available"
            }
        }

        viewModel.weather.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> weatherTextView.text = "Loading weather..."
                is Resource.Success -> {
                    val data = resource.data
                    if (data != null) {
                        val temperatureCelsius = data.main.temp - 273.15
                        val temperatureText = "%.2f°C".format(temperatureCelsius)

                        weatherTextView.text = """
                    Weather: ${data.weather[0].description.capitalize()}
                    Temperature: $temperatureText
                    Wind Speed: ${data.wind.speed} m/s
                """.trimIndent()
                    } else {
                        weatherTextView.text = "Weather data unavailable"
                    }
                }
                is Resource.Error -> {
                    weatherTextView.text = "Weather error: ${resource.message}"
                }
            }
        }


        if (locationProvider.hasLocationPermission()) {
            fetchLocation()
        } else {
            locationProvider.requestLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE)
        }

        historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchLocation() {
        locationProvider.getCurrentLocation { location: Location? ->
            viewModel.updateLocation(location)
            if (location == null) {
                Toast.makeText(this, "Could not get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchAddressFromCoordinates(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                val city = address.locality ?: "Unknown City"
                val state = address.adminArea ?: "Unknown State"
                locationTextView.text = "$city, $state"
            } else {
                locationTextView.text = "Address not available"
            }
        } catch (e: Exception) {
            locationTextView.text = "Failed to get address"
        }
    }

    private fun saveWeatherToDatabase() {
        Log.d("WeatherDB", "saveWeatherToDatabase() called")
        val locationData = locationTextView.text.toString()
        val weatherData = weatherTextView.text.toString()
        Log.d("WeatherDB", "Location text: $locationData")
        Log.d("WeatherDB", "Weather text: $weatherData")

        val weatherResource = viewModel.weather.value
        if (weatherResource is Resource.Success && weatherResource.data != null) {
            val data = weatherResource.data

            val temperatureCelsius = data.main.temp - 273.15
            val description = data.weather[0].description.capitalize()
            val humidity = data.main.humidity
            val windSpeed = data.wind.speed
            val date = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())

            val weatherEntity = SavedWeather(
                date = date,
                locationName = locationData,
                description = description,
                temperatureCelsius = temperatureCelsius,
                humidity = humidity,
                windSpeed = windSpeed
            )

            Log.d("WeatherDB", "Prepared weather entity to save: $weatherEntity")

            viewModel.saveWeather(weatherEntity)
            Toast.makeText(this, "Weather saved successfully", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("WeatherDB", "No weather data available to save")
            Toast.makeText(this, "No weather data to save", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val API_KEY = "367963667bef68bfdff8412f50c23979"
    }
}