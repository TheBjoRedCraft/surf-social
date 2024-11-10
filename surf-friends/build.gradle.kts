import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("dev.slne.java-common")
    id("dev.slne.java-shadow")
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

dependencies {
    compileOnlyApi(libs.paper.api)
    compileOnly(libs.commandapi.bukkit)
    implementation(libs.inventory.framework)

    implementation ("com.zaxxer:HikariCP:5.0.1")
    implementation ("mysql:mysql-connector-java:8.0.33")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
}

paper {
    main = "dev.slne.surf.friends.SurfFriendsPlugin"
    apiVersion = "1.21"
    authors = listOf("TheBjoRedCraft", "SLNE Development")
    prefix = "SurfSocial/SurfFriends"
    name = "SurfFriends"

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
    archiveVersion.set("1.0.0-SNAPSHOT")
    archiveBaseName.set("surf-friends")
}
