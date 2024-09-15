plugins {
    `java-library`
    `maven-publish`

    id("net.linguica.maven-settings")
    id("dev.slne.gradle-properties")
}

group = "dev.slne"
version = "1.21-1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.slne.dev/repository/maven-proxy/") { name = "maven-proxy" }
}

dependencies {
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    withSourcesJar()
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.compilerArgs.add("-parameters")
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}
