package com.popov.volumepresets

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val vm = remember { ViewModel() }
    MaterialTheme {
        MainWindow(vm, onCloseRequest = ::exitApplication)
        if (vm.settingsWindowOpened.collectAsState().value) SettingsWindow(vm = vm)
    }
}

@Composable
@Preview
fun MainWindow(vm: ViewModel, onCloseRequest: () -> Unit) {
    val strings by vm.strings.collectAsState()
    Window(
        onCloseRequest = onCloseRequest,
        state = rememberWindowState(size = DpSize(350.dp, 600.dp)),
        title = strings.appName
    ) {
        Box(
            modifier = Modifier.background(MaterialTheme.colors.background).padding(16.dp).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Text(strings.setVolumeTitle, textAlign = TextAlign.Center, style = MaterialTheme.typography.h5)

                vm.volumePresets.collectAsState().value.forEach { volumePreset ->
                    Button(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        onClick = { vm.setVolume(volumePreset) }
                    ) {
                        Text(modifier = Modifier.padding(12.dp), text = "$volumePreset%")
                    }
                }

                Divider()

                TextButton(
                    onClick = { vm.settingsWindowOpened.value = true },
                    enabled = !vm.settingsWindowOpened.collectAsState().value
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = strings.settingsButton)
                    Text(" " + strings.settingsButton)
                }
            }
        }
    }
}
