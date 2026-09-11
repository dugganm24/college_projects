package com.bignerdranch.android.geoquiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // This now hosts the NavHostFragment
    }
}

//Moved Logic for fragment implementation