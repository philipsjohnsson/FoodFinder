package se.umu.cs.phjo0015.mapapplication.database

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Shared ViewModel that provides access to the reminders in the database for all fragments
 */
public class DestinationViewModel(application: Application) : AndroidViewModel(application) {
    lateinit var destinations: LiveData<List<Destination>>
    private val dao: DestinationDao

    fun getDestination(destinationId: Int): LiveData<Destination> {
        return dao[destinationId]
    }

    fun getDestinations() {

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

        val destinationsToInsert: List<Destination> = getDataset()

        // Add default destinations in a background thread.
        //viewModelScope.launch(Dispatchers.IO) {
            //val currentDestinations = dao.getAllDestinationsSync()

            //if(currentDestinations.isEmpty()) {
                //destinationsToInsert.forEach { destination ->
                    //dao.insert(destination)
               // }
            //}
        //}
    }
}