plugins {
    id("buildx-multiplatform-library")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
    id("org.jetbrains.kotlinx.kover")
    id("org.jetbrains.dokka")
}

description = "cryptography-kotlin random API"

kotlin {
    allTargets()

    linuxX64 {
        cinterop("random", "linux")
    }
}
