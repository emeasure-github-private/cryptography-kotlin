plugins {
    id("buildx-multiplatform")
}

kotlin {
    allTargets()
    sourceSets {
        commonTest {
            dependencies {
                implementation(projects.cryptographyTests.cryptographyTestUtils)
            }
        }
    }
}