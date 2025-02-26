package dev.slne.surf.social.chat

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.jorel.commandapi.CommandAPI
import dev.slne.surf.social.chat.command.*
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

import java.security.SecureRandom

class SurfChat : SuspendingJavaPlugin() {
    override fun onEnable() {
        CommandAPI.unregister("msg")
        CommandAPI.unregister("tell")
        CommandAPI.unregister("w")

        PrivateMessageCommand("msg").register()
        ChannelCommand("channel").register()
        SurfChatCommand("surfchat").register()
        IgnoreCommand("ignore").register()
        TogglePmCommand("togglepm").register()
        ReplyCommand("reply").register()

        this.saveDefaultConfig()

        ChatFilterService.loadBlockedWords()
        DatabaseService.connect()

        Bukkit.getPluginManager().registerEvents(PlayerAsyncChatListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerQuitListener(), this)
    }

    override suspend fun onDisableAsync() {
        DatabaseService.saveAll()
        DatabaseService.disconnect()
    }

    companion object {
        private val random: SecureRandom = SecureRandom()

        val instance: SurfChat get() = getPlugin(SurfChat::class.java)

        fun send(player: OfflinePlayer, text: MessageBuilder, messageID: Int = 0) {
            val message = Colors.PREFIX.append(text.build())

            if (player.isOnline) {
                val onlinePlayer = player.player ?: return

                onlinePlayer.sendMessage(message)

                ChatHistoryService.insertNewMessage(player.uniqueId, Message("Unknown", player.name ?: player.uniqueId.toString(), message), messageID)
            }
        }
    }
}
