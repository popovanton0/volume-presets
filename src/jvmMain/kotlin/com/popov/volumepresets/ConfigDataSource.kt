package com.popov.volumepresets

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.prefs.Preferences

@OptIn(DelicateCoroutinesApi::class)
class ConfigDataSource {
    private val configNode = Preferences.userRoot().node("com.popov:volume-presets")

    private var _lang: Lang
        get() = Lang.valueOf(configNode.get("lang", "EN"))
        set(value) = configNode.put("lang", value.name)

    private var _volumePresets: List<UInt>
        get() = configNode.get("volumePresets", "0;20;50;70;100").split(';').map { it.toUInt() }
        set(value) = configNode.put("volumePresets", value.joinToString(separator = ";"))


    val lang = MutableStateFlow<Lang>(_lang)
    val volumePresets = MutableStateFlow<List<UInt>>(_volumePresets)

    init {
        with(GlobalScope) {
            launch { lang.collect { _lang = it } }
            launch { volumePresets.collect { _volumePresets = it } }
        }
    }

    fun resetToDefaults() {
        lang.value = Lang.EN
        volumePresets.value = listOf(0u, 20u, 70u, 100u)
    }
}