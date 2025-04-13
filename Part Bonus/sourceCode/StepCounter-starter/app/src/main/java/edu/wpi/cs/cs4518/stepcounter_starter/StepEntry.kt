package edu.wpi.cs.cs4518.stepcounter_starter

import androidx.room.Entity
import androidx.room.PrimaryKey

//Entity annotation tells Room that this is a database table
@Entity
data class StepEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primary key field
    val timestamp: Long, //time of step detection in milliseconds
    val steps: Int //number of steps detected
)