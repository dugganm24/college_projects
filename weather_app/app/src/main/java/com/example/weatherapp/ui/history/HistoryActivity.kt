package com.example.weatherapp.ui.history

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.App
import com.example.weatherapp.R
import com.example.weatherapp.data.local.SavedWeatherDao
import com.example.weatherapp.data.location.LocationProvider
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.ui.main.MainActivity

class HistoryActivity : AppCompatActivity() {

    private lateinit var backButton: Button
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyViewModel: HistoryViewModel

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Initialize the back button
        backButton = findViewById(R.id.backButton)

        // Initialize RecyclerView
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        historyAdapter = HistoryAdapter(emptyList()) // Initialize with an empty list
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        // Initialize ViewModel with repository
        val database = (application as App).database
        val savedWeatherDao = database.savedWeatherDao()
        val locationProvider = LocationProvider(this)
        val repository = WeatherRepository(locationProvider, savedWeatherDao)
        historyViewModel = HistoryViewModel(repository)

        // Observe the saved weather list
        historyViewModel.savedWeatherList.observe(this) { historyList ->
            historyAdapter.updateData(historyList) // Update adapter with new data
        }

        historyViewModel.fetchSavedWeather() // Fetch saved weather data

        // Set up the click listener for the back button
        backButton.setOnClickListener {
            // Navigate back to MainActivity (current weather screen)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the current activity (HistoryActivity) to remove it from the back stack
        }
    }
}
