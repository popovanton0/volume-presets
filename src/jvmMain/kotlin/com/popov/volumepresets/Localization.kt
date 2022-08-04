package com.popov.volumepresets

enum class Lang(val strings: Strings) { RU(RuStrings), EN(EnStrings) }

interface Strings {
    val appName:String get() = "Volume Presets"
    val setVolumeTitle: String
    val settingsButton: String

    val settings_audioDevice: String
    val settings_allAudioDevices: String

    val settings_language: String
    fun langName(lang: Lang): String

    val settings_volume_presets_title: String
    val settings_volume_presets_add: String
    val settings_volume_presets_delete: String

    val settings_save: String
    val settings_resetToDefaults: String

    val settings_version: String
    val settings_createdBy: String
    val settings_githubLink: String get() = "https://github.com/popovanton0/volume-presets"
}

object RuStrings : Strings {
    override val setVolumeTitle: String = "Установить громкость на"
    override val settingsButton: String = "Настройки"

    override val settings_audioDevice: String = "Аудиоустройство"
    override val settings_allAudioDevices: String = "Все аудиоустройства"

    override val settings_language: String = "Язык: "
    override fun langName(lang: Lang) = when (lang) {
        Lang.RU -> "Русский"
        Lang.EN -> "Английский"
    }

    override val settings_volume_presets_title: String = "Уровни громкости"
    override val settings_volume_presets_add: String = "Добавить"
    override val settings_volume_presets_delete: String = "Удалить"

    override val settings_save: String = "Сохранить"
    override val settings_resetToDefaults: String = "Сбросить настройки"

    override val settings_version: String = "Версия"
    override val settings_createdBy: String = "Создано Антоном Поповым"
}

object EnStrings : Strings {
    override val setVolumeTitle: String = "Set volume at"
    override val settingsButton: String = "Settings"

    override val settings_audioDevice: String = "Audio Device"
    override val settings_allAudioDevices: String = "All audio devices"

    override val settings_language: String = "Language: "
    override fun langName(lang: Lang) = when (lang) {
        Lang.RU -> "Russian"
        Lang.EN -> "English"
    }

    override val settings_volume_presets_title: String = "Volume presets"
    override val settings_volume_presets_add: String = "Add"
    override val settings_volume_presets_delete: String = "Delete"

    override val settings_save: String = "Save"
    override val settings_resetToDefaults: String = "Reset to defaults"

    override val settings_version: String = "Version"
    override val settings_createdBy: String = "Created by Anton Popov"
}