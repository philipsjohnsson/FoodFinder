package se.umu.cs.phjo0015.mapapplication.model

import androidx.compose.runtime.MutableState

data class SettingsToggle(
    var title: String,
    val onToggle: () -> Unit,
    var isEnabled: MutableState<Boolean>
)
