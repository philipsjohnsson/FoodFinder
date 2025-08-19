package se.umu.cs.phjo0015.mapapplication

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView

@Composable
fun FilledButtonExample(onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.size(width = 100.dp, height = 52.dp)
    ) {
        Text("Filled")
    }
}

@Composable
fun OsmdroidMapView(context: Context) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val mapView = MapView(context)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setBuiltInZoomControls(true)
            mapView.setMultiTouchControls(true)

            val mapController = mapView.controller
            mapController.setZoom(10.0)
            mapController.setCenter(org.osmdroid.util.GeoPoint(63.8258, 20.2630))

            mapView
        }
    )
}