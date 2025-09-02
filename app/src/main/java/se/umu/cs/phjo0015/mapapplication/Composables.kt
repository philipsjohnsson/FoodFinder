package se.umu.cs.phjo0015.mapapplication

import android.graphics.Bitmap
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.bonuspack.utils.BonusPackHelper
import org.osmdroid.views.overlay.CopyrightOverlay
import se.umu.cs.phjo0015.mapapplication.model.UserLocation
// import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import kotlin.random.Random
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import se.umu.cs.phjo0015.mapapplication.database.Destination

// Inspired by:
// https://stackoverflow.com/questions/77297775/create-a-map-using-openstreetmap-with-jetpack-compose-in-kotlin-programming-lang
// https://github.com/MKergall/osmbonuspack/wiki/Tutorial_0
// https://github.com/MKergall/osmbonuspack/wiki/HowToInclude

@Composable
fun OsmdroidMapView(
    callbackOnMarkerClick: (Destination) -> Boolean,
    destinations: State<List<Destination>>,
    userLocationState: State<UserLocation?>
) {
    AndroidView(
        factory = { context ->
            val mapView = MapView(context)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setBuiltInZoomControls(true)
            mapView.setMultiTouchControls(true)

            val mapController = mapView.controller
            mapController.setZoom(5.0)
            mapController.setCenter(GeoPoint(63.189460, 14.607896))

            // CLUSTER: https://github.com/MKergall/osmbonuspack/wiki/Tutorial_3
            // To edit this cluster design look at the part 11 in the link above.
            val poiMarkers: RadiusMarkerClusterer = RadiusMarkerClusterer(context)
            //val testClusterIcon = ContextCompat.getDrawable(context, R.drawable.restaurant_icon)

            // Save reference for poiMarkers
            mapView.setTag(R.id.poi_markers, poiMarkers)

            val clusterIcon: Bitmap = BonusPackHelper.getBitmapFromVectorDrawable(context, R.drawable.marker_cluster)
            poiMarkers.setIcon(clusterIcon)

            mapView.overlays.add(poiMarkers)

            val copyRightOverlay = CopyrightOverlay(context)
            mapView.overlays.add(copyRightOverlay)

            mapView.invalidate()

            return@AndroidView mapView
        },
        update = { mapView ->

            val poiMarkers = mapView.getTag(R.id.poi_markers) as RadiusMarkerClusterer

            // Remove old markers
            poiMarkers.items.clear()

            // Add new markers when the data has fetched from the database
            destinations.value.forEach { destination ->
                val marker = Marker(mapView)
                val destinationPoint = GeoPoint(destination.lat, destination.long)

                marker.position = destinationPoint
                marker.icon = ContextCompat.getDrawable(mapView.context, R.drawable.restaurant_icon)
                marker.title = destination.topic
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                marker.setOnMarkerClickListener{ m, _ ->
                    callbackOnMarkerClick(destination)
                }

                poiMarkers.add(marker)
            }

            // Remove old location Marker
            val oldMarker = mapView.getTag(R.id.user_marker) as? Marker
            if (oldMarker != null) {
                mapView.overlays.remove(oldMarker)
                mapView.setTag(R.id.user_marker, null)
            }

            // Add location marker for user
            userLocationState.value?.let { userLocation ->
                val userPoint = GeoPoint(userLocation.latitude, userLocation.longitude)
                val userMarker = Marker(mapView).apply {
                    position = userPoint
                    title = "Användarens plats"
                    icon = ContextCompat.getDrawable(mapView.context, R.drawable.dot_location)
                }

                mapView.overlays.add(userMarker)
                mapView.controller.setCenter(userPoint)
                mapView.controller.setZoom(8.0)
                mapView.setTag(R.id.user_marker, userMarker)
            }

            poiMarkers.invalidate()
            mapView.invalidate()
        }
    )
}
