package se.umu.cs.phjo0015.mapapplication.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class representing a destination with a lat,long,topic and description content.
 * Annotated with Entity so can be stored in a database managed by Room
 */
@Entity
class Destination(
    var lat: Double,
    var long: Double,
    var topic: String = "",
    var description: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}