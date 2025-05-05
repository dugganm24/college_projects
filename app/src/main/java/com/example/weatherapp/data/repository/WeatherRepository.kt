package com.example.weatherapp.data.repository

import com.example.weatherapp.data.api.WeatherApiClient
import com.example.weatherapp.data.location.LocationProvider
import com.example.weatherapp.utils.Resource
import com.example.weatherapp.data.api.models.WeatherResponse
import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class WeatherRepository(
    private val locationProvider: LocationProvider
) {

    suspend fun getWeather(apiKey: String): Resource<WeatherResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val location = getLocation()
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude

                    // Fetch weather data
                    val response = WeatherApiClient.apiService.getWeather(lat, lon, apiKey)
                    if (response.isSuccessful) {
                        Resource.Success(response.body()!!)
                    } else {
                        Resource.Error("Failed to fetch weather data")
                    }
                } else {
                    Resource.Error("Failed to get location")
                }
            } catch (e: Exception) {
                Resource.Error("An error occurred: ${e.message}")
            }
        }
    }

    private suspend fun getLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            locationProvider.getCurrentLocation { location ->
                continuation.resume(location)
            }
        }
    }
}
