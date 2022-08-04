package com.popov.volumepresets

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(DelicateCoroutinesApi::class)
class ViewModel {
    private val dataSource = ConfigDataSource()
    private val volumeController = VolumeController

    val volumeControlName: MutableStateFlow<String?> = dataSource.volumeControlName
    val volumePresets: MutableStateFlow<List<UInt>> = dataSource.volumePresets
    val settingsWindowOpened = MutableStateFlow<Boolean>(false)
    val lang: MutableStateFlow<Lang> = dataSource.lang
    val strings = dataSource.lang
        .map { it.strings }
        .stateIn(GlobalScope, SharingStarted.Eagerly, EnStrings)

    fun getAudioDeviceNames(): List<String> {
        val names = mutableListOf<String>()
        volumeController.getVolumeControls { it.forEach { names += it.displayName } }
        return names
    }

    fun setVolume(volumePreset: UInt) {
        val presetFloat = volumePreset.toFloat() / 100
        volumeController.getVolumeControls { controls ->
            if (volumeControlName.value != null) {
                val volumeControl = controls.find { it.displayName == volumeControlName.value }
                (volumeControl ?: controls.firstOrNull())?.volume = presetFloat
            } else {
                controls.forEach { it.volume = presetFloat }
            }
        }
    }

    fun resetToDefaults() = dataSource.resetToDefaults()
}