package com.example.weatherapp.ui.main

import android.location.Location
import androidx.lifecycle.*
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.api.models.WeatherResponse
import com.example.weatherapp.utils.Resource
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.example.weatherapp.data.local.SavedWeather

class MainViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> get() = _location

    private val _weather = MutableLiveData<Resource<WeatherResponse>>()
    val weather: LiveData<Resource<WeatherResponse>> get() = _weather

    fun updateLocation(location: Location?) {
        _location.value = location
    }

    fun fetchWeather(apiKey: String) {
        viewModelScope.launch {
            Log.d("MainViewModel", "Fetching weather data with API Key: $apiKey")
            _weather.value = Resource.Loading()

            val weatherResponse = weatherRepository.getWeather(apiKey)
            _weather.value = weatherResponse // Update _weather with the result
        }
    }

    fun saveWeather(weather: SavedWeather) {
        Log.d("WeatherDB", "ViewModel: saving weather: $weather")
        viewModelScope.launch {
            weatherRepository.saveWeather(weather)
        }
    }


}