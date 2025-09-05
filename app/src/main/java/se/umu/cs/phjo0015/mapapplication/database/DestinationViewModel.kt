package se.umu.cs.phjo0015.mapapplication.database

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.AndroidViewModel
import androidx.room.Room

/**
 * Shared ViewModel that provides access to the destinations in the database for all fragments
 */
public class DestinationViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: DestinationDao
    var destinations: LiveData<List<Destination>>

    fun getDestination(destinationId: Int): LiveData<Destination> {
        return dao[destinationId]
    }

    suspend fun getDestinationSync(destinationId: Int): Destination? {
        return dao.getDestinationSync(destinationId)
    }

    /**
     * Provides access to the Room database instance.
     *
     * Initializes the database if it hasn't been created yet, and returns the existing instance otherwise.
     *
     * @return The Room database instance.
     */
    val database: Database
        get() {

            getApplication<Application>().deleteDatabase("destinationDB")

            if (db == null) {
                db = Room.databaseBuilder(
                    getApplication<Application>().applicationContext,
                    Database::class.java, "destinationDB"
                )
                    .createFromAsset("database/destination.db")
                    .build()
            }
            return db!!
        }

    companion object {
        private var db: Database? = null
    }

    init {
        dao = database.destinationDao()
        destinations = dao.allDestinations
    }
}