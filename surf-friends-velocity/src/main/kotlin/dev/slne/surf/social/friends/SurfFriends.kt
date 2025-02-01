package dev.slne.surf.social.friends

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIVelocityConfig
import dev.slne.surf.social.friends.database.Database
import org.slf4j.Logger
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

@Plugin(id = "surf-friends", name = "SurfFriends", version = "4.0.0", authors = ["SLNE Development", "TheBjoRedCraft"])
class SurfFriends @Inject constructor (
    suspendingPluginContainer: SuspendingPluginContainer,
    val proxy: ProxyServer,
    val logger: Logger,
    @DataDirectory val dataDirectory: Path
) {
    lateinit var config: Path
    lateinit var node: CommentedConfigurationNode
    lateinit var loader: YamlConfigurationLoader

    init {
        instance = this
        suspendingPluginContainer.initialize(this)
        CommandAPI.onLoad(CommandAPIVelocityConfig(proxy, this))
    }

    companion object {
        @JvmStatic
        lateinit var instance: SurfFriends
            private set
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        this.createDefaultConfig()
        Database.createConnection()
        CommandAPI.onEnable()
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        this.saveConfig()
        Database.closeConnection()
        CommandAPI.onDisable()
    }

    private fun createDefaultConfig() {
        if (Files.notExists(dataDirectory)) {
            Files.createDirectories(dataDirectory)
        }

        config = dataDirectory.resolve("config.yml")

        if (Files.notExists(config)) {
            try {
                val stream: InputStream =
                    this.javaClass.getClassLoader().getResourceAsStream("config.yml") ?: return

                Files.copy(stream, config)
            } catch (e: Exception) {
                logger.error("Failed to copy config file", e)
            }
        }

        loader = YamlConfigurationLoader.builder().path(config).build()
        node = loader.load()
    }

    private fun saveConfig() {
        loader.save(node)
    }
}