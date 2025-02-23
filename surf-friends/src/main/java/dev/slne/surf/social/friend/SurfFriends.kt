package dev.slne.surf.social.friend

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIVelocityConfig
import dev.slne.surf.social.friend.command.SurfFriendCommand
import dev.slne.surf.social.friend.command.friend.FriendCommand
import dev.slne.surf.social.friend.service.ConfigService
import dev.slne.surf.social.friend.service.DatabaseService
import dev.slne.surf.social.friend.util.Colors
import dev.slne.surf.social.friend.util.MessageBuilder

import org.slf4j.Logger
import java.nio.file.Path
import java.util.*


val pluginContainer: PluginContainer get() = SurfFriends.instance.pluginContainer

@Plugin(
    id = "surf-friends",
    name = "SurfFriends",
    version = "1.21.4-5.0.0-SNAPSHOT",
    authors = ["SLNE Development"]
)
class SurfFriends @Inject constructor(
    val proxyServer: ProxyServer,
    val logger: Logger,
    @DataDirectory val dataDirectory: Path,
    suspendingPluginContainer: SuspendingPluginContainer,
    val pluginContainer: PluginContainer
) {
    val configService: ConfigService
    val databaseService: DatabaseService

    init {
        suspendingPluginContainer.initialize(this)
        instance = this

        CommandAPI.onLoad(CommandAPIVelocityConfig(proxyServer, this))

        configService = ConfigService(dataDirectory)
        databaseService = DatabaseService()
    }

    @Subscribe
    fun onStartup(event: ProxyInitializeEvent?) {
        CommandAPI.onEnable()

        configService.createDefaultConfig()
        configService.loadConfig()

        databaseService.connect()

        SurfFriendCommand("surffriend").register()
        FriendCommand("friend").register()
    }

    @Subscribe
    fun onShutdown(event: ProxyShutdownEvent?) {
        CommandAPI.onDisable()

        databaseService.disconnect()
    }

    companion object {
        lateinit var instance: SurfFriends

        fun send(player: Player, messageBuilder: MessageBuilder) {
            player.sendMessage(Colors.PREFIX.append(messageBuilder.build()))
        }

        fun send(player: UUID?, messageBuilder: MessageBuilder) {
            val proxyServer: ProxyServer = this.instance.proxyServer
            val velocityPlayer = proxyServer.getPlayer(player).orElse(null) ?: return

            velocityPlayer.sendMessage(Colors.PREFIX.append(messageBuilder.build()))
        }
    }
}
