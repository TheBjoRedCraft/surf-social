package dev.slne.surf.friends

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import dev.slne.surf.friends.command.FriendCommand
import dev.slne.surf.friends.command.subcommand.FriendAddCommand
import dev.slne.surf.friends.command.subcommand.FriendListCommand
import dev.slne.surf.friends.database.Database
import dev.slne.surf.friends.listener.PlayerJoinListener
import dev.slne.surf.friends.listener.PlayerQuitListener
import dev.slne.surf.friends.listener.util.PluginColor
import kotlinx.coroutines.DelicateCoroutinesApi
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

val plugin: SurfFriendsPlugin
    get() = JavaPlugin.getPlugin(SurfFriendsPlugin::class.java)

val prefix = Component.text(">> ", NamedTextColor.GRAY)
    .append(Component.text("Friends", PluginColor.BLUE_LIGHT))
    .append(Component.text(" | ", NamedTextColor.DARK_GRAY))

class SurfFriendsPlugin : SuspendingJavaPlugin() {
    override fun onEnable() {
        this.registerCommands()
        this.registerListener()

        saveDefaultConfig()
        Database.createConnection()
    }

    override suspend fun onDisableAsync() {
        FriendManager.saveAll(true)
    }

    private fun registerCommands() {
        FriendCommand("friend").register()
        FriendAddCommand("fa").register()
        FriendListCommand("fl").register()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun registerListener() {
        Bukkit.getPluginManager().registerSuspendingEvents(PlayerQuitListener, this)
        Bukkit.getPluginManager().registerSuspendingEvents(PlayerJoinListener, this)
    }
}
