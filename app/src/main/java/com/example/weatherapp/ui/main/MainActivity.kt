package com.example.weatherapp.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weatherapp.R
import com.example.weatherapp.data.location.LocationProvider
import java.util.Locale
import com.example.weatherapp.ui.history.HistoryActivity

class MainActivity : AppCompatActivity() {

    private lateinit var locationProvider: LocationProvider
    private val viewModel: MainViewModel by viewModels()

    private lateinit var locationTextView: TextView
    private lateinit var historyButton: Button // Reference for the History Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        locationTextView = findViewById(R.id.locationTextView)
        historyButton = findViewById(R.id.historyButton) // Initialize the history button

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        locationProvider = LocationProvider(this)

        viewModel.location.observe(this) { location ->
            // Update the UI with the location information
            if (location != null) {
                fetchAddressFromCoordinates(location)
            } else {
                locationTextView.text = "Location not available"
            }
        }

        // Automatically request location on startup if permissions are granted
        if (locationProvider.hasLocationPermission()) {
            fetchLocation()  // Get location if permission granted
        } else {
            locationProvider.requestLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE)  // Request permission if not granted
        }

        // Set up the listener for the History button
        historyButton.setOnClickListener {
            // Navigate to the History Activity when the button is clicked
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchLocation() {
        locationProvider.getCurrentLocation { location: Location? ->
            // Update the viewModel with the fetched location
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

    // Handle location permission result
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
    }
}
