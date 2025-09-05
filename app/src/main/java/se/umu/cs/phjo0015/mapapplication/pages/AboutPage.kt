package se.umu.cs.phjo0015.mapapplication.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

@Composable
fun AboutPage() {
    Column (
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column {
            Text(
                "Version",
                color = Color(0xFF497349),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "1.0",
                style = MaterialTheme.typography.titleMedium
            )
            Text("Appens syfte",
                color = Color(0xFF497349),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
                )
            Text(
                "Upptäck nya resmål med vår interaktiva karta! " +
                "Hitta vackra, intressanta och spännande platser att besöka, " +
                "få inspiration för nästa resa och upptäck världen på ett enkelt och roligt sätt. " +
                "Perfekt för dig som älskar att resa och upptäcka världen!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic
                )
            )
        }
    }
}