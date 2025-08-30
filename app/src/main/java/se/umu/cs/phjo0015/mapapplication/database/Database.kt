package se.umu.cs.phjo0015.mapapplication.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for destinations
 * */
@Database(entities = [Destination::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun destinationDao(): DestinationDao
}
