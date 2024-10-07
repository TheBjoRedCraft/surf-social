plugins {
    id("java")
    id("dev.slne.java-common")
    id("dev.slne.java-shadow")
}

dependencies {
    api(project(":surf-friends:surf-friends-core"))

    annotationProcessor(libs.velocity.api.annotation)
    compileOnlyApi(libs.velocity.api)

    implementation(libs.commandapi.velocity)
    implementation(libs.fastutil)
}

tasks.shadowJar {
    archiveClassifier.set("")
}