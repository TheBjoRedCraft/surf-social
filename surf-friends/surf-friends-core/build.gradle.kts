plugins {
    id("java")
    id("dev.slne.java-common")
    id("dev.slne.java-shadow")
}

dependencies {
    compileOnlyApi(libs.velocity.api)
    compileOnlyApi(libs.paper.api)

    implementation(libs.fastutil)
}

tasks.shadowJar {
    relocate("it.unimi.dsi.fastutil", "dev.slne.surf.friends.core.fastutil")

    archiveClassifier.set("")
}
