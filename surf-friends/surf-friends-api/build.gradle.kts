plugins {
    id("java")
    id("dev.slne.java-common")
    id("dev.slne.java-shadow")
}

dependencies {
    compileOnlyApi(libs.velocity.api)
    compileOnlyApi(libs.paper.api)
}