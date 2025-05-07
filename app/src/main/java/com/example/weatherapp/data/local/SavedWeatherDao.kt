package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SavedWeatherDao {

    @Insert
    suspend fun insertWeather(savedWeather: SavedWeather)

    @Query("SELECT * FROM saved_weather ORDER BY id DESC")
    suspend fun getAllSavedWeather(): List<SavedWeather>
}
