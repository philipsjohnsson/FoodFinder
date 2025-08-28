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

@Composable
fun FilledButtonExample(onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.size(width = 100.dp, height = 52.dp)
    ) {
        Text("Filled")
    }
}

// Inspired by:
// https://stackoverflow.com/questions/77297775/create-a-map-using-openstreetmap-with-jetpack-compose-in-kotlin-programming-lang
// https://github.com/MKergall/osmbonuspack/wiki/Tutorial_0
// https://github.com/MKergall/osmbonuspack/wiki/HowToInclude

@Composable
fun OsmdroidMapView(
    callbackOnMarkerClick: (Marker) -> Boolean,
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

            val clusterIcon: Bitmap = BonusPackHelper.getBitmapFromVectorDrawable(context, R.drawable.marker_cluster)
            poiMarkers.setIcon(clusterIcon)

            /**
             *

            val points = listOf(
                GeoPoint(63.8258, 20.2630),
                GeoPoint(63.8260, 20.2632),
                GeoPoint(63.8255, 20.2635),
                GeoPoint(63.8262, 20.2628),
                GeoPoint(63.8257, 20.2631),
                GeoPoint(63.8259, 20.2633),
                GeoPoint(63.8261, 20.2630),
                GeoPoint(63.8256, 20.2632),
                GeoPoint(63.8263, 20.2634),
                GeoPoint(63.8254, 20.2629),
                GeoPoint(63.8258, 20.2635),
                GeoPoint(63.8260, 20.2627),
                GeoPoint(63.8257, 20.2628),
                GeoPoint(63.8262, 20.2631),
                GeoPoint(63.8255, 20.2630)
            )
            */

            val randomPoints = List(100) {
                val lat = 63.8258 + Random.nextDouble(-0.001, 0.001) // +/- 0.001 ~ ca 100 m
                val lon = 20.2630 + Random.nextDouble(-0.001, 0.001)
                GeoPoint(lat, lon)
            }

            var userPosition: GeoPoint? = null
            println(userLocationState.value?.latitude)
            println(userLocationState)

            for (point in randomPoints) {
                val marker = Marker(mapView)
                marker.position = point
                marker.icon = ContextCompat.getDrawable(context, R.drawable.restaurant_icon)
                marker.title = "Marker"
                poiMarkers.add(marker)

                marker.setOnMarkerClickListener{ m, _ ->
                    callbackOnMarkerClick(m)
                }
            }

            mapView.overlays.add(poiMarkers)

            val copyRightOverlay = CopyrightOverlay(context)
            mapView.overlays.add(copyRightOverlay)

            mapView.invalidate()

            return@AndroidView mapView
        },
        update = { mapView ->

            userLocationState.value?.let { userLocation ->
                val userPoint = GeoPoint(userLocation.longitude, userLocation.latitude)
                val userMarker = Marker(mapView).apply {
                    position = userPoint
                    title = "Användarens plats"
                    icon = ContextCompat.getDrawable(mapView.context, R.drawable.restaurant_icon)
                }
                // poiMarkers.add(marker)
                mapView.overlays.add(userMarker)
                mapView.controller.setCenter(userPoint)
                mapView.controller.setZoom(8.0)
            }

            mapView.invalidate()
        }
    )
}
