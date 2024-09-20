import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("dev.slne.java-common")
    id("dev.slne.java-shadow")
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

dependencies {
    api(project(":surf-friends:surf-friends-core"))

    compileOnlyApi(libs.paper.api)
    compileOnlyApi(libs.commandapi.bukkit)

    implementation(libs.inventory.framework)
}

paper {
    main = "dev.slne.surf.friends.paper.FriendsPaperPlugin"
    apiVersion = "1.21"
    authors = listOf("TheBjoRedCraft", "SLNE Development")
    prefix = "SurfSocial/SurfFriends"

    serverDependencies {
        register("CommandAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = true;
        }
    }
}

tasks.shadowJar{
    relocate("com.github.stefvanschie.inventoryframework", "dev.slne.surf.friends.paper.inventoryframework")

    archiveClassifier.set("")
}