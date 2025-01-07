import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("com.gradleup.shadow")
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("org.hibernate.build.maven-repo-auth") version "3.0.4"

    `kotlin-dsl`
    `maven-publish`
}

repositories {
    mavenCentral()
    gradlePluginPortal()

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }

    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")

    maven("https://repo.slne.dev/repository/maven-unsafe/") {
        name = "maven-unsafe"
    }
}


dependencies {
    compileOnlyApi(libs.paper.api)
    compileOnlyApi(libs.packetuxui)
    compileOnly(libs.commandapi.bukkit)

    implementation(libs.inventory.framework)
    implementation(libs.repo.auth)

    implementation ("com.zaxxer:HikariCP:5.0.1")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    implementation(kotlin("stdlib-jdk8"))
    implementation("dev.hsbrysk:caffeine-coroutines:1.2.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.20.0")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.20.0")

    implementation("net.wesjd:anvilgui:1.10.3-SNAPSHOT")
}

paper {
    main = "dev.slne.surf.friends.SurfFriendsPlugin"
    apiVersion = "1.21"
    authors = listOf("TheBjoRedCraft", "SLNE Development")
    prefix = "SurfSocial/SurfFriends"
    name = "SurfFriends"
    version = "3.2.0-SNAPSHOT"
    foliaSupported = false

    serverDependencies {
        register("CommandAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true
        }
    }
}

tasks.shadowJar {
    relocate("com.github.stefvanschie.inventoryframework", "dev.slne.surf.friends.inventoryframework")

    archiveClassifier.set("")
    archiveVersion.set("3.2.0-SNAPSHOT")
    archiveBaseName.set("surf-friends")

    manifest {
        attributes["paperweight-mappings-namespace"] = "spigot"
    }
}

kotlin {
    jvmToolchain(21)
}