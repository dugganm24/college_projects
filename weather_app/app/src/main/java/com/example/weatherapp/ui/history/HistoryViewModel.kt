package com.example.weatherapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.data.local.SavedWeather
import com.example.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryViewModel(private val repository: WeatherRepository) : ViewModel() {

    // LiveData to hold the list of saved weather data
    private val _savedWeatherList = MutableLiveData<List<SavedWeather>>()
    val savedWeatherList: LiveData<List<SavedWeather>> get() = _savedWeatherList

    // LiveData to hold errors, if any
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Fetch the saved weather data from the repository
    fun fetchSavedWeather() {
        viewModelScope.launch {
            try {
                // Using Dispatchers.IO to perform database query on background thread
                val result = withContext(Dispatchers.IO) {
                    repository.getAllSavedWeather()
                }
                _savedWeatherList.value = result // Update the LiveData on successful fetch
            } catch (e: Exception) {
                _error.value = "Failed to load saved weather data: ${e.message}" // Handle errors and update error LiveData
            }
        }
    }

    // Optional: A function to refresh the weather data (in case you want to reload data)
    fun refreshSavedWeather() {
        fetchSavedWeather() // Simply calls fetchSavedWeather again
    }
}
