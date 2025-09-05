package se.umu.cs.phjo0015.mapapplication.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import org.osmdroid.util.GeoPoint

class MapState(
    private var center: GeoPoint?,
    private var zoom: Double,
    private var hasCenteredOnUser: Boolean
): Parcelable {

    @RequiresApi(Build.VERSION_CODES.Q)
    private constructor(parcel: Parcel) : this (
        center = parcel.readParcelable<GeoPoint>(GeoPoint::class.java.classLoader)!!,
        zoom = parcel.readDouble(),
        hasCenteredOnUser = parcel.readBoolean()
    ) {

    }

    fun setZoom(zoomLvl: Double) {
        zoom = zoomLvl
    }

    fun setCenter(geoPoint: GeoPoint) {
        center = geoPoint
    }

    fun setHasCenteredOnUser(bol: Boolean) {
        hasCenteredOnUser = bol
    }

    fun getHasCenteredOnUser(): Boolean {
        return hasCenteredOnUser
    }

    fun getZoom(): Double {
        return zoom
    }

    fun getCenter(): GeoPoint? {
        return center
    }

    override fun describeContents(): Int = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(center, flags)
        parcel.writeDouble(zoom)
        parcel.writeBoolean(hasCenteredOnUser)
    }

    companion object CREATOR : Parcelable.Creator<MapState> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): MapState {
            return MapState(parcel)
        }

        override fun newArray(size: Int): Array<MapState?> {
            return arrayOfNulls(size)
        }

    }
}