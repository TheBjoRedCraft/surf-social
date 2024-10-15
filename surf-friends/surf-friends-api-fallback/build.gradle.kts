plugins {
    id("java")
    id("dev.slne.java-common")
    id("dev.slne.java-shadow")
}

dependencies {
    api(project(":surf-friends:surf-friends-api"))
    api(project(":surf-friends:surf-friends-core"))

    compileOnlyApi(libs.velocity.api)
    compileOnlyApi(libs.velocity.api.annotation)

    implementation(libs.fastutil)
}

repositories {
    mavenCentral()
}

tasks.shadowJar {
    relocate("it.unimi.dsi.fastutil", "dev.slne.surf.friends.api.fallback.fastutil")

    archiveClassifier.set("")
    archiveVersion.set("")
}