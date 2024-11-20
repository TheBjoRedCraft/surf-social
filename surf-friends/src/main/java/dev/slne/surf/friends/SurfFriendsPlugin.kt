package dev.slne.surf.friends

import dev.slne.surf.friends.command.FriendCommand
import dev.slne.surf.friends.command.subcommand.FriendAddCommand
import dev.slne.surf.friends.command.subcommand.FriendListCommand
import dev.slne.surf.friends.config.PluginConfig
import dev.slne.surf.friends.database.Database
import dev.slne.surf.friends.listener.PlayerJoinListener
import dev.slne.surf.friends.listener.PlayerQuitListener
import dev.slne.surf.friends.listener.util.PluginColor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.future.await
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class SurfFriendsPlugin : JavaPlugin() {
    override fun onEnable() {
        this.registerCommands()
        this.registerListener()

        PluginConfig.createConfig()
        Database.createConnection()
    }

    override fun onDisable() {
        runBlocking {
            FriendManager.instance.saveAll(true)
        }
    }

    private fun registerCommands() {
        FriendCommand("friend").register()
        FriendAddCommand("fa").register()
        FriendListCommand("fl").register()
    }

    private fun registerListener() {
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerJoinListener(), this)
    }

    companion object {
        val prefix: Component
            get() = Component.text(">> ").color(NamedTextColor.GRAY)
                .append(
                    Component.text("Friends").color(PluginColor.BLUE_LIGHT)
                )
                .append(
                    Component.text(" | ").color(NamedTextColor.DARK_GRAY)
                )

        val instance: SurfFriendsPlugin get() = getPlugin(SurfFriendsPlugin::class.java)
    }
}
