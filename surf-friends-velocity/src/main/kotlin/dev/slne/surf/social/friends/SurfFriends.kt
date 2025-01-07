package dev.slne.surf.social.friends

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.social.friends.database.Database
import dev.slne.surf.social.friends.manager.FriendManager

import org.slf4j.Logger
import org.spongepowered.configurate.CommentedConfigurationNode
import org.spongepowered.configurate.yaml.YamlConfigurationLoader

import java.nio.file.Files
import java.nio.file.Path

@Plugin(id = "surf-friends", name = "SurfFriends", version = "4.0.0", authors = ["SLNE Development", "TheBjoRedCraft"])
class SurfFriends
@Inject constructor (
    val proxy: ProxyServer,
    val logger: Logger,
    var friendManager: FriendManager,
    var config: Path,
    var node: CommentedConfigurationNode,
    private var loader: YamlConfigurationLoader,
    @DataDirectory private val dataDirectory: Path
) {

    init {
        instance = this
    }

    companion object {
        @JvmStatic
        lateinit var instance: SurfFriends
            private set
    }

    @Subscribe
    suspend fun onProxyInitialization(event: ProxyInitializeEvent) {
        this.createDefaultConfig()

        Database.createConnection()
    }

    @Subscribe
    suspend fun onProxyShutdown(event: ProxyShutdownEvent) {
        this.saveConfig()

        Database.closeConnection()
    }

    private suspend fun createDefaultConfig() {
        if (Files.notExists(dataDirectory)) {
            Files.createDirectories(dataDirectory)
        }

        config = dataDirectory.resolve("config.yml")

        if (Files.notExists(config)) {
            try {
                val stream = this.javaClass.getClassLoader().getResourceAsStream("config.yml")
                Files.copy(stream, config)
            } catch (e: Exception) {
                logger.error("Failed to copy config file", e)
            }
        }

        loader = YamlConfigurationLoader.builder().path(config).build()
        node = loader.load()

    }

    private suspend fun saveConfig() {
        loader.save(node)
    }
}