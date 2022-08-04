package com.popov.volumepresets

internal class VolumeController {
    fun setVolume(volumePreset: UInt) {
        when (osType) {
            OSType.MacOS -> setVolumeMacOs(volumePreset)
            OSType.Windows -> setVolumeWindows(volumePreset)
            else -> error("os is not supported")
        }
    }

    private fun setVolumeMacOs(volumePreset: UInt) {
        val value = 7 * (volumePreset.toFloat() / 100)
        runCatching { ProcessBuilder("osascript", "-e", "set volume $value").start() }
    }

    private fun setVolumeWindows(volumePreset: UInt) {
        val value = UShort.MAX_VALUE.toFloat() * (volumePreset.toFloat() / 100)
        runCatching {
            val appResourcesPath = System.getProperty("compose.application.resources.dir")
            ProcessBuilder("$appResourcesPath/nircmdc.exe", "setsysvolume", "$value").start()
        }
    }
}