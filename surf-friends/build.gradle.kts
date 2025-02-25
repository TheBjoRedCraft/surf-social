plugins {
    id("java")
    id ("com.gradleup.shadow") version "9.0.0-beta8"
    kotlin("jvm") version "2.1.10"
    kotlin("kapt") version "2.1.10"
}

group = "dev.slne"
version = "unspecified"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    compileOnly(files("libs/offline-velocity-1.0.0.jar"))

    kapt("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    implementation("dev.jorel:commandapi-velocity-shade:9.7.1-SNAPSHOT")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")
    implementation("it.unimi.dsi:fastutil:8.5.9")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.electronwill.night-config:toml:3.6.5")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("dev.hsbrysk:caffeine-coroutines:2.0.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-velocity-api:2.21.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-velocity-core:2.21.0")
    implementation ("mysql:mysql-connector-java:8.0.33")
}

tasks.shadowJar {
    archiveFileName = "surf-friends-" + project.version + ".jar"

    relocate("dev.jorel.commandapi", "dev.slne.surf.social.friends.libs")
}

kotlin {
    jvmToolchain(21)
}