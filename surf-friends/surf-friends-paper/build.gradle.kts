import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("dev.slne.java-common")
    id("dev.slne.java-shadow")
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("io.freefair.lombok") version "8.10"
}

dependencies {
    api(project(":surf-friends:surf-friends-core"))

    compileOnlyApi(libs.paper.api)
    compileOnlyApi(libs.commandapi.bukkit)
}

paper {
    main = "dev.slne.surf.friends.paper.FriendsPaperPlugin"
    apiVersion = "1.20"
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
    archiveClassifier.set("")
}