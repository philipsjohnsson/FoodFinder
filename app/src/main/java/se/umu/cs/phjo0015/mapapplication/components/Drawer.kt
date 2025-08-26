package se.umu.cs.phjo0015.mapapplication.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DrawerContent(
    callbackOnClickDrawerItem: (DrawerItem) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Text("Header", modifier = Modifier.padding(bottom = 16.dp))

        DrawerItem("Inställningar") {
            callbackOnClickDrawerItem(DrawerItem.SETTINGS)
        }
    }
}

@Composable
fun DrawerItem(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    )
}

enum class DrawerItem() {
    SETTINGS
}