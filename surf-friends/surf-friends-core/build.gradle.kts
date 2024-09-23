plugins {
    id("java")
    id("dev.slne.java-common")
    id("dev.slne.java-shadow")
}

dependencies {
    api(project(":surf-friends:surf-friends-api"))

    compileOnlyApi(libs.velocity.api)
    compileOnlyApi(libs.paper.api)
}
