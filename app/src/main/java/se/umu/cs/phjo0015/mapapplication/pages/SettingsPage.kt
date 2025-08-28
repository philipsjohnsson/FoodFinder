package se.umu.cs.phjo0015.mapapplication.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.umu.cs.phjo0015.mapapplication.components.SwitchMinimalExample
import se.umu.cs.phjo0015.mapapplication.model.SettingsToggle

@Composable
fun SettingsPage(
    settingToggles: List<SettingsToggle>
) {
    Column (
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Kartinställningar",
            color = Color(0xFF497349),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        for(setting in settingToggles) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    setting.title,
                    style = MaterialTheme.typography.titleMedium
                )
                SwitchMinimalExample(setting.onToggle, setting.isEnabled)
            }
        }
    }
}