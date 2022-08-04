package com.popov.volumepresets

import javax.sound.sampled.*

internal object VolumeController {
    fun getVolumeControls(block: (List<VolumeControl>) -> Unit) {
        var volumeControls: List<VolumeControl>? = null
        try {
            volumeControls = volumeControls()
            block(volumeControls)
        } catch (_: Throwable) {
        } finally {
            volumeControls?.forEach { it.close() }
        }
    }

    private fun volumeControls(): List<VolumeControl> {
        val volumeControls = mutableListOf<VolumeControl>()
        mixers().forEach { mixer ->
            getAvailableOutputLines(mixer).forEach { line ->
                open(line)
                val control = runCatching { getVolumeControl(line) }.getOrNull()
                if (control != null) volumeControls += VolumeControl(mixer, line, control)
            }
        }
        return volumeControls
    }

    private fun open(line: Line) {
        try {
            if (!line.isOpen) line.open()
        } catch (_: LineUnavailableException) {
        }
    }

    class VolumeControl(
        mixer: Mixer,
        private val line: Line,
        private val control: FloatControl,
    ) {
        val displayName = mixer.mixerInfo.run { "$name ($description)" }
        var volume: Float
            get() = control.value
            set(value) {
                require(value in 0f..1f)
                control.value = value
            }

        fun close() = runCatching { line.close() }
    }

    private fun mixers() = AudioSystem.getMixerInfo().map { AudioSystem.getMixer(it) }

    private fun getVolumeControl(line: Line): FloatControl? {
        if (!line.isOpen) throw RuntimeException("Line is closed: " + line.lineInfo.toString())
        return findControl(FloatControl.Type.VOLUME, *line.controls) as FloatControl?
    }

    private fun findControl(type: Control.Type, vararg controls: Control): Control? {
        if (controls.isEmpty()) return null
        for (control in controls) {
            if (control.type == type) return control
            if (control is CompoundControl) {
                val member = findControl(type, *control.memberControls)
                if (member != null) return member
            }
        }
        return null
    }

    private fun getAvailableOutputLines(mixer: Mixer): List<Line> = mixer.targetLineInfo.mapNotNull {
        try {
            mixer.getLine(it)
        } catch (ex: LineUnavailableException) {
            null
        }
    }
}