package com.example.weatherapp.ui.history

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.R
import com.example.weatherapp.ui.main.MainActivity

class HistoryActivity : AppCompatActivity() {

    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Initialize the back button
        backButton = findViewById(R.id.backButton)

        // Set up the click listener for the back button
        backButton.setOnClickListener {
            // Navigate back to MainActivity (current weather screen)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the current activity (HistoryActivity) to remove it from the back stack
        }
    }
}
