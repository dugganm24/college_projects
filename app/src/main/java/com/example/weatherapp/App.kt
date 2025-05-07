package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.data.local.WeatherDatabase

class App : Application() {

    lateinit var database: WeatherDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = WeatherDatabase.getDatabase(this)
    }
}
