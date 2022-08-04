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
    private val volumeController = VolumeController()

    val volumePresets: MutableStateFlow<List<UInt>> = dataSource.volumePresets
    val settingsWindowOpened = MutableStateFlow<Boolean>(false)
    val lang: MutableStateFlow<Lang> = dataSource.lang
    val strings = dataSource.lang
        .map { it.strings }
        .stateIn(GlobalScope, SharingStarted.Eagerly, EnStrings)

    fun resetToDefaults() = dataSource.resetToDefaults()

    fun setVolume(volumePreset: UInt) = volumeController.setVolume(volumePreset)
}