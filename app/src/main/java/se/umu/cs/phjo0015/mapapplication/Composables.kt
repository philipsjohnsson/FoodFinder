package se.umu.cs.phjo0015.mapapplication

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilledButtonExample(onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.size(width = 100.dp, height = 52.dp)
    ) {
        Text("Filled")
    }
}