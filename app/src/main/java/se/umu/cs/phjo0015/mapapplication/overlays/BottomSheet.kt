package se.umu.cs.phjo0015.mapapplication.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import se.umu.cs.phjo0015.mapapplication.components.ImageCarouselLazy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetWithDrag(callbackSetBottomSheetVisible: (Boolean) -> Unit) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    // Open with animation
    LaunchedEffect(Unit) {
        scaffoldState.bottomSheetState.expand() // Expand with animation
    }

    // Some inspiration from:
    // https://stackoverflow.com/questions/77830986/drag-a-modalbottomsheet-to-full-screen-in-jetpack-compose
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 140.dp,
        sheetContent = {
            Box(
                modifier = Modifier.fillMaxHeight(0.9f)
            ) {
                Column(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(10.dp)
                ) {
                    TopForBottomSheet(callbackSetBottomSheetVisible, scaffoldState)
                    Text("Restaurang")
                    ImageCarouselLazy()
                }
            }
        }
    ) { paddingValues ->
    }
}

// Mby place this in components later instead..
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopForBottomSheet(callbackSetBottomSheetVisible: (Boolean) -> Unit, scaffoldState:  BottomSheetScaffoldState) {
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Test",
            fontSize = 28.sp
        )
        FilledIconButton(
            onClick = {
                callbackSetBottomSheetVisible(false)
            }
        ) {
            Icon(Icons.Default.Close , contentDescription = "close")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BtmSheet() {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    //if (showDialog.value) {
        ModalBottomSheet(
            onDismissRequest = {
                //showDialog.value = false
            },
            sheetState = sheetState
        ) {

            // Sheet content
            Button(onClick = {
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        // showDialog.value = false
                    }
                }
            }) {
                Text("Hide bottom sheet")
            }
        }
    //}
}