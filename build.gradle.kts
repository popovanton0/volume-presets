import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.popov"
version = "1.1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
                @Suppress("SuspiciousCollectionReassignment")
                freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
            }
        }
        withJava()
    }
    @Suppress("UNUSED_VARIABLE") sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "com.popov.volumepresets.MainKt"
        nativeDistributions {
            modules("java.instrument", "java.prefs", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Volume Presets"
            packageVersion = project.version.toString()
            description = "Control your volume levels easily, using presets"
            vendor = "Anton Popov"
            copyright = "Copyright (C) 2022, Anton Popov"
            licenseFile.set(project.file("LICENSE.txt"))
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))
            windows {
                perUserInstall = true
            }
        }
    }
}
