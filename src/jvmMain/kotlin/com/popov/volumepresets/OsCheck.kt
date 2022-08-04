package com.popov.volumepresets

private var detectedOS: OSType? = null
val osType: OSType?
    get() {
        if (detectedOS == null) {
            val os = System.getProperty("os.name", "generic").lowercase()
            detectedOS = when {
                os.contains("mac") || os.contains("darwin") -> OSType.MacOS
                os.contains("win") -> OSType.Windows
                os.contains("nux") -> OSType.Linux
                else -> OSType.Other
            }
        }
        return detectedOS
    }

enum class OSType {
    Windows, MacOS, Linux, Other
}