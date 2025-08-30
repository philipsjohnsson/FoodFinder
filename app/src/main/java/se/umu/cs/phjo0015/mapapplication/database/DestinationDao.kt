package se.umu.cs.phjo0015.mapapplication.database

import androidx.room.Dao
import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DestinationDao {
    @get:Query("SELECT * from destination order by id DESC")
    val all: LiveData<List<Destination>>

    @Query("SELECT * FROM destination WHERE id = :param")
    operator fun get(param: Int): LiveData<Destination>

    @Insert
    fun insert(destination: Destination)

    @Delete
    fun delete(destination: Destination)

}