package se.umu.cs.phjo0015.mapapplication.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

@SuppressLint("UnrememberedMutableState")
@Composable
fun SwitchMinimalExample(
    toggleShowUserLocation: () -> Unit,
    isEnabled: MutableState<Boolean>
) {

    Switch(
        modifier = Modifier.scale(0.7f),
        checked = isEnabled.value,
        onCheckedChange = { isChecked: Boolean ->
            toggleShowUserLocation()
        }
    )
}