package edu.wpi.cs.cs4518.stepcounter_starter

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

//Data Access Object interface for database operations
@Dao
interface StepDao {
    //Insert a new step entry into the database
    @Insert
    suspend fun insert(entry: StepEntry)

    //Query to get the total steps for the current day
    @Query("SELECT SUM(steps) FROM StepEntry WHERE date(timestamp / 1000, 'unixepoch') = date('now')")
    suspend fun getTodayTotalSteps(): Int?

    //Query to get all step entries in ascending order by timestamp
    @Query("SELECT * FROM StepEntry ORDER BY timestamp ASC")
    suspend fun getAllEntries(): List<StepEntry>
}
