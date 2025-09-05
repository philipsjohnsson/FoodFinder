package se.umu.cs.phjo0015.mapapplication.pages

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
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
import androidx.compose.runtime.State
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import se.umu.cs.phjo0015.mapapplication.R
import se.umu.cs.phjo0015.mapapplication.database.Destination
import se.umu.cs.phjo0015.mapapplication.model.MapState

// Inspired by:
// https://stackoverflow.com/questions/77297775/create-a-map-using-openstreetmap-with-jetpack-compose-in-kotlin-programming-lang
// https://github.com/MKergall/osmbonuspack/wiki/Tutorial_0
// https://github.com/MKergall/osmbonuspack/wiki/HowToInclude

@Composable
fun osmdroidMapView(
    callbackOnMarkerClick: (Destination) -> Boolean,
    destinations: State<List<Destination>>,
    userLocationState: State<UserLocation?>,
    mapState: MapState
) {
    var mapViewRef: MapView? = null

    AndroidView(
        factory = { context ->
            val mapView = MapView(context)
            mapViewRef = mapView

            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setBuiltInZoomControls(true)
            mapView.setMultiTouchControls(true)

            val mapController = mapView.controller
            mapController.setZoom(mapState.getZoom())
            mapController.setCenter(mapState.getCenter() ?: GeoPoint(63.189460, 14.607896))

            mapView.addMapListener(object : MapListener {
                override fun onScroll(event: ScrollEvent?): Boolean {
                    mapState.setCenter(mapView.mapCenter as GeoPoint)

                    return true
                }

                override fun onZoom(event: ZoomEvent?): Boolean {
                    mapState.setZoom(mapView.zoomLevelDouble)

                    return true
                }
            })

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

            mapView.controller.setZoom(mapState.getZoom())
            mapView.controller.setCenter(mapState.getCenter())

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
                mapView.setTag(R.id.user_marker, userMarker)

                if(!mapState.getHasCenteredOnUser()) {

                    mapView.controller.setZoom(15.0)
                    mapView.controller.setCenter(userPoint)
                    mapState.setZoom(15.0)
                    mapState.setCenter(userPoint)

                    mapState.setHasCenteredOnUser(true)
                }
            }

            poiMarkers.invalidate()
            mapView.invalidate()
        }
    )
}
