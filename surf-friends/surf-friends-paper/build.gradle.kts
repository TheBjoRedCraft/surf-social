plugins {
    id("java")
    id("dev.slne.java-common")
    id("dev.slne.java-shadow")
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

dependencies {
    implementation(project(":surf-friends:surf-friends-core"))


    compileOnlyApi(libs.paper.api)
    implementation(libs.inventory.framework)
}

paper {
    main = "dev.slne.surf.friends.paper.PaperInstance"
    apiVersion = "1.21"
    authors = listOf("TheBjoRedCraft", "SLNE Development")
    prefix = "SurfSocial/SurfFriends"
}

tasks.shadowJar{
    relocate("com.github.stefvanschie.inventoryframework", "dev.slne.surf.friends.paper.inventoryframework")

    archiveClassifier.set("")
    archiveVersion.set("")
}
