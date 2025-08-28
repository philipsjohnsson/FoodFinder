package se.umu.cs.phjo0015.mapapplication.components

import android.annotation.SuppressLint
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@SuppressLint("UnrememberedMutableState")
@Composable
fun SwitchMinimalExample(
    toggleShowUserLocation: () -> Unit,
    isEnabled: MutableState<Boolean>
) {

    Switch(
        checked = isEnabled.value,
        onCheckedChange = { isChecked: Boolean ->
            toggleShowUserLocation()
        }
    )
}