package edu.wpi.cs.cs4518.stepcounter_starter

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Defines database schema and version
@Database(entities = [StepEntry::class], version = 1)
abstract class StepDatabase : RoomDatabase() {

    // abstract method that returns the DAO
    abstract fun stepDao(): StepDao

    companion object {
        // Volatile ensures visibility across threads
        @Volatile
        private var INSTANCE: StepDatabase? = null

        // Singleton instance of database
        fun getDatabase(context: Context): StepDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StepDatabase::class.java,
                    "step_db" // Name of the database file
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
