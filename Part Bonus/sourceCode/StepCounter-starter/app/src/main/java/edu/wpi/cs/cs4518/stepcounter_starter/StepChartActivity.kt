package edu.wpi.cs.cs4518.stepcounter_starter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class StepChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_chart)

        // Add StepChartFragment only if not already added (e.g. after rotation)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StepChartFragment())
                .commit()
        }
    }
}
