package dev.slne.surf.social.chat

import dev.jorel.commandapi.CommandAPI
import dev.slne.surf.social.chat.command.IgnoreCommand
import dev.slne.surf.social.chat.command.PrivateMessageCommand
import dev.slne.surf.social.chat.command.SurfChatCommand
import dev.slne.surf.social.chat.command.TogglePmCommand
import dev.slne.surf.social.chat.command.channel.ChannelCommand
import dev.slne.surf.social.chat.listener.PlayerAsyncChatListener
import dev.slne.surf.social.chat.listener.PlayerQuitListener
import dev.slne.surf.social.chat.`object`.Message
import dev.slne.surf.social.chat.service.ChatFilterService
import dev.slne.surf.social.chat.service.ChatHistoryService
import dev.slne.surf.social.chat.service.DatabaseService
import dev.slne.surf.social.chat.util.Colors
import dev.slne.surf.social.chat.util.MessageBuilder

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.plugin.java.JavaPlugin

class SurfChat : JavaPlugin() {
    override fun onEnable() {
        CommandAPI.unregister("msg")
        CommandAPI.unregister("tell")
        CommandAPI.unregister("w")

        PrivateMessageCommand("msg").register()
        ChannelCommand("channel").register()
        SurfChatCommand("surfchat").register()
        IgnoreCommand("ignore").register()
        TogglePmCommand("togglepm").register()

        this.saveDefaultConfig()

        ChatFilterService.getInstance().loadBlockedWords()
        DatabaseService.getInstance().connect()

        Bukkit.getPluginManager().registerEvents(PlayerAsyncChatListener(), this)
        Bukkit.getPluginManager().registerEvents(
            PlayerQuitListener(),
            this
        )
    }

    override fun onDisable() {
        DatabaseService.getInstance().disconnect()
    }

    companion object {
        private val random: java.security.SecureRandom = java.security.SecureRandom()

        val instance: SurfChat
            get() = getPlugin<SurfChat>(SurfChat::class.java)

        fun send(player: OfflinePlayer, text: MessageBuilder) {
            val message = Colors.PREFIX.append(text.build())

            if (player.isOnline) {
                player.player.sendMessage(message)

                ChatHistoryService.getInstance().insertNewMessage(
                    player.uniqueId,
                    Message("Unknown", player.name, message),
                    random.nextInt(1000000)
                )
            }
        }
    }
}
