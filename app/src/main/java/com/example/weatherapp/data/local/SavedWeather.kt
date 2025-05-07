package com.example.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_weather")
data class SavedWeather(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val locationName: String,
    val description: String,
    val temperatureCelsius: Double,
    val humidity: Int,
    val windSpeed: Double
)
