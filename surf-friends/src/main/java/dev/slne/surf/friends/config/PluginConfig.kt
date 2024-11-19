package dev.slne.surf.friends.config

import dev.slne.surf.friends.SurfFriendsPlugin
import org.bukkit.configuration.file.FileConfiguration

object PluginConfig {
    fun config(): FileConfiguration {
        return SurfFriendsPlugin.instance.getConfig()
    }

    fun createConfig() {
        SurfFriendsPlugin.instance.saveDefaultConfig()
    }
}
