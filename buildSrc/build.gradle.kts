plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("net.linguica.gradle:maven-settings-plugin:0.5")
    implementation("org.gradle.kotlin:gradle-kotlin-dsl-plugins:4.2.1")
    implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.1")
    implementation("io.freefair.gradle:lombok-plugin:8.10")
}