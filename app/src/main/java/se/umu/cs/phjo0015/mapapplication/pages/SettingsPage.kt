package se.umu.cs.phjo0015.mapapplication.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import se.umu.cs.phjo0015.mapapplication.components.SwitchMinimalExample
import se.umu.cs.phjo0015.mapapplication.model.SettingsToggle

@Composable
fun SettingsPage(
    settingToggles: List<SettingsToggle>
) {
    Column {
        for(setting in settingToggles) {
            Row {
                Text(setting.title)
                SwitchMinimalExample(setting.onToggle, setting.isEnabled)
            }
        }
    }
}