plugins {
    id("dev.slne.java-common")
    id("dev.slne.java-shadow")
}

dependencies {
    api(project(":surf-friends:surf-friends-core"))

    compileOnlyApi(libs.velocity.api)
    compileOnlyApi(libs.commandapi.velocity)
}