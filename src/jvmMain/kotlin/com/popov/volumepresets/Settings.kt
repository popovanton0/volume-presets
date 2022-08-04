package com.popov.volumepresets

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import java.awt.Desktop
import java.net.URL

private data class Preset(val text: String) {
    val isValid: Boolean = run {
        val volumePreset = text.toUIntOrNull()
        volumePreset != null && volumePreset in 0u..100u
    }
}

@Composable
fun SettingsWindow(vm: ViewModel) {
    val volumePresets = remember { vm.volumePresets.value.map { Preset(text = it.toString()) }.toMutableStateList() }
    fun saveAndClose() {
        if (volumePresets.all { it.isValid }) vm.volumePresets.value = volumePresets.toList().map { it.text.toUInt() }
        vm.settingsWindowOpened.value = false
    }

    val strings by vm.strings.collectAsState()
    Window(
        title = strings.settingsButton,
        onCloseRequest = ::saveAndClose,
        state = rememberWindowState(size = DpSize(400.dp, 800.dp))
    ) {
        Box(
            modifier = Modifier.background(MaterialTheme.colors.background).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.verticalScroll(scrollState).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                LanguageSetting(strings, vm)
                Divider()
                PresetsSetting(volumePresets, strings)
                Divider()
                About(strings)
                Divider()

                Button(
                    onClick = ::saveAndClose,
                    enabled = volumePresets.all { it.isValid }
                ) {
                    Text(strings.settings_save)
                }
                TextButton(onClick = {
                    vm.resetToDefaults()
                    vm.settingsWindowOpened.value = false
                }) {
                    Text(strings.settings_resetToDefaults)
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(scrollState)
            )
        }
    }
}

@Composable
private fun About(strings: Strings) {
    Text("${strings.settings_version} 1.1.0", style = MaterialTheme.typography.body2)
    Text(strings.settings_createdBy, style = MaterialTheme.typography.body2)
    Text(
        modifier = Modifier.clickable { openWebpage(strings.settings_githubLink) },
        text = strings.settings_githubLink,
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline,
        color = MaterialTheme.colors.primary, style = MaterialTheme.typography.body2,
    )
}

@Composable
private fun LanguageSetting(strings: Strings, vm: ViewModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(strings.settings_language)
        val currentLang by vm.lang.collectAsState()
        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Lang.values().forEach { lang ->
                Row(
                    modifier = Modifier.clickable { vm.lang.value = lang },
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = currentLang == lang, onClick = null)
                    Text(vm.strings.value.langName(lang))
                }
            }
        }
    }
}

@Composable
private fun PresetsSetting(volumePresets: SnapshotStateList<Preset>, strings: Strings) = Column(
    horizontalAlignment = Alignment.Start,
    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
) {
    Text(
        modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally),
        text = strings.settings_volume_presets_title
    )
    volumePresets.forEachIndexed { index, volumePreset ->
        key(index) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    modifier = Modifier.width(100.dp),
                    value = volumePreset.text,
                    onValueChange = { volumePresets[index] = Preset(it.take(3)) },
                    singleLine = true,
                    trailingIcon = { Text("%") },
                    isError = !volumePreset.isValid,
                )
                Spacer(Modifier.width(8.dp))
                Delete(volumePresets, index, strings)
                MoveUp(index, volumePresets, volumePreset, strings)
                MoveDown(index, volumePresets, volumePreset, strings)
            }
        }
    }

    TextButton(onClick = { volumePresets.add(Preset("0")) }) {
        Icon(imageVector = Icons.Default.Add, contentDescription = strings.settings_volume_presets_add)
        Text(" " + strings.settings_volume_presets_add)
    }
}

@Composable
private fun MoveDown(index: Int, volumePresets: SnapshotStateList<Preset>, volumePreset: Preset, strings: Strings) {
    if (index != volumePresets.lastIndex) IconButton(onClick = {
        volumePresets.removeAt(index)
        volumePresets.add(index + 1, volumePreset)
    }) {
        Icon(imageVector = Icons.Default.KeyboardArrowDown, strings.settings_volume_presets_delete)
    }
}

@Composable
private fun MoveUp(index: Int, volumePresets: SnapshotStateList<Preset>, volumePreset: Preset, strings: Strings) {
    if (index > 0) IconButton(onClick = {
        volumePresets.removeAt(index)
        volumePresets.add(index - 1, volumePreset)
    }) {
        Icon(imageVector = Icons.Default.KeyboardArrowUp, strings.settings_volume_presets_delete)
    }
}

@Composable
private fun Delete(volumePresets: SnapshotStateList<Preset>, index: Int, strings: Strings) {
    if (volumePresets.size != 1) IconButton(onClick = { volumePresets.removeAt(index) }) {
        Icon(imageVector = Icons.Default.Delete, strings.settings_volume_presets_delete)
    }
}

private fun openWebpage(urlString: String) {
    try {
        Desktop.getDesktop().browse(URL(urlString).toURI())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}