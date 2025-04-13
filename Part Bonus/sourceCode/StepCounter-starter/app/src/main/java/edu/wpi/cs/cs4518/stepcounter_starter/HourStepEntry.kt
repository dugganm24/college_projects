package edu.wpi.cs.cs4518.stepcounter_starter

// This class holds the result for hourly aggregated step counts
data class HourStepEntry(
    val hour: String,       // Hour of the day
    val totalSteps: Int     // Sum of steps recorded in that hour
)
