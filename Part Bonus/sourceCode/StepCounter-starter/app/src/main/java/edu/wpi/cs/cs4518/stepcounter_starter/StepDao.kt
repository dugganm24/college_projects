package edu.wpi.cs.cs4518.stepcounter_starter

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

//Data Access Object interface for database operations
@Dao
interface   StepDao {
    //Insert a new step entry into the database
    @Insert
    suspend fun insert(entry: StepEntry)

    //Query to get the total steps for the current day
    @Query("SELECT SUM(steps) FROM StepEntry WHERE date(timestamp / 1000, 'unixepoch') = date('now')")
    suspend fun getTodayTotalSteps(): Int?

    //Query to get all step entries in ascending order by timestamp
    @Query("SELECT * FROM StepEntry ORDER BY timestamp ASC")
    suspend fun getAllEntries(): List<StepEntry>

    //delete all data from the database
    @Query("DELETE FROM StepEntry")
    suspend fun clearAllEntries()

    //Query to get all step entries
    @Query("SELECT * FROM StepEntry")
    suspend fun getAll(): List<StepEntry>

    //Query to get the hourly aggregation of steps
    @Query("""
    SELECT 
        CAST(strftime('%H', timestamp / 1000, 'unixepoch') AS INTEGER) AS hour,
        SUM(steps) as totalSteps
    FROM StepEntry
    WHERE strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch') = :date
    GROUP BY hour
    ORDER BY hour
""")
    suspend fun getHourlySteps(date: String): List<HourStepEntry>
}
