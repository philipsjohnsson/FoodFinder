package se.umu.cs.phjo0015.mapapplication.overlays

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun InfoDialogExample(
    onCloseDialog: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    Dialog(onDismissRequest = onCloseDialog) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                Text(text = "Rubrik")

            }
        }
    }
}