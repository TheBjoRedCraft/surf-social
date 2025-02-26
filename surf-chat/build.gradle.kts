import dev.slne.surf.surfapi.gradle.util.registerRequired

plugins {
    id("java")
    id("com.gradleup.shadow")
    kotlin("jvm")
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

val versionFile = file("version.txt")
val incrementVersion = project.findProperty("incrementVersion")?.toString()?.toBoolean() ?: true

val versionParts = versionFile.readText().trim().split(".")
val major = versionParts[0].toInt()
val minor = versionParts[1].toInt()
var patch = versionParts[2].toInt()

if (incrementVersion) {
    patch += 1
}

val newVersion = "$major.$minor.$patch"

group = "dev.slne"
version = "$newVersion-1.21.4-SNAPSHOT"

repositories {
    mavenCentral()

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        name = "codemc"
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.extendedclip.com/releases/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.7.0")
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.21.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.21.0")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation("dev.hsbrysk:caffeine-coroutines:2.0.0")
    implementation("dev.jorel:commandapi-bukkit-kotlin:9.7.0")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.social.chat.SurfChat")
    authors.add("SLNE Development")
}


tasks {
    shadowJar {
        archiveFileName = "${project.name}-${project.version}.jar"

        if (incrementVersion) {
            dependsOn("incrementVersion")
        }
    }
}

tasks.register("incrementVersion") {
    doLast {
        versionFile.writeText(newVersion)
        println("Incremented version to $newVersion")
    }
}

kotlin {
    jvmToolchain(21)
}