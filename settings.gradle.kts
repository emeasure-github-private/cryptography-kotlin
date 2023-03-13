enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    includeBuild("build-logic")
    includeBuild("build-parameters")
    includeBuild("build-kotlin")
}

plugins {
    id("kotlin-version-catalog")
    id("com.gradle.enterprise") version "3.12.2"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

rootProject.name = "cryptography-kotlin"

include("cryptography-bom")
include("cryptography-version-catalog")
include("cryptography-random")
include("cryptography-core")

//providers
listOf("jdk", "apple", "webcrypto").forEach { name ->
    include("cryptography-providers:cryptography-$name")
    project(":cryptography-providers:cryptography-$name").projectDir = file("cryptography-providers/$name")
}

listOf("api", "shared", "prebuilt", "test").forEach { name ->
    include("cryptography-providers:cryptography-openssl3:cryptography-openssl3-$name")
    project(":cryptography-providers:cryptography-openssl3:cryptography-openssl3-$name").projectDir =
        file("cryptography-providers/openssl3/$name")
}

//tests
listOf(
    "test-utils",
    "behavior-tests",
    "compatibility-tests"
).forEach { name ->
    include("cryptography-tests:cryptography-$name")
    project(":cryptography-tests:cryptography-$name").projectDir = file("cryptography-tests/$name")
}

//test tool
listOf("client", "server").forEach { name ->
    include("cryptography-tester:cryptography-tester-$name")
    project(":cryptography-tester:cryptography-tester-$name").projectDir = file("cryptography-tester/$name")
}
