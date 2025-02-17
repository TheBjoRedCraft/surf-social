package dev.slne.surf.social.chat.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.social.chat.`object`.HistoryPair
import dev.slne.surf.social.chat.`object`.Message
import it.unimi.dsi.fastutil.objects.Object2ObjectMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class ChatHistoryService {
    private val chatHistoryCache: Cache<UUID, Object2ObjectMap<HistoryPair, Message>?> =
        Caffeine
            .newBuilder()
            .build<java.util.UUID, Object2ObjectMap<HistoryPair, Message>?>()

    fun clearInternalChatHistory(player: java.util.UUID) {
        chatHistoryCache.invalidate(player)
    }

    fun clearChat() {
        chatHistoryCache.invalidateAll()

        Bukkit.getOnlinePlayers().forEach { player: Player ->
            var index = 0
            while (index < 100) {
                player.sendMessage(Component.empty())
                index++
            }
        }
    }

    fun insertNewMessage(player: java.util.UUID, message: Message, messageID: Int) {
        var chatHistory: Object2ObjectMap<HistoryPair, Message>? =
            chatHistoryCache.getIfPresent(player)

        if (chatHistory == null) {
            chatHistory = Object2ObjectOpenHashMap<HistoryPair, Message>()
            chatHistoryCache.put(player, chatHistory)
        }

        val timestamp: Int = (java.lang.System.currentTimeMillis() / 1000).toInt()
        chatHistory!!.put(HistoryPair(messageID, timestamp.toLong()), message)
    }


    fun removeMessage(player: java.util.UUID, messageID: Int) {
        val chatHistory: Object2ObjectMap<HistoryPair, Message>? =
            chatHistoryCache.getIfPresent(player)

        chatHistory?.entries?.removeIf { entry: Map.Entry<HistoryPair, Message?> -> entry.key.getMessageID() == messageID }
    }

    fun resend(player: java.util.UUID) {
        val chatHistory: Object2ObjectMap<HistoryPair, Message>? =
            chatHistoryCache.getIfPresent(player)

        Bukkit.getOnlinePlayers().forEach { online: Player ->
            var index = 0
            while (index < 100) {
                online.sendMessage(Component.empty())
                index++
            }
        }

        chatHistory?.object2ObjectEntrySet()?.stream()?.sorted(
            java.util.Comparator.comparingLong<Object2ObjectMap.Entry<HistoryPair, Message>>(
                java.util.function.ToLongFunction<Object2ObjectMap.Entry<HistoryPair, Message>> { entry: Object2ObjectMap.Entry<HistoryPair, Message?> -> entry.key.getSendTime() })
        )
            ?.forEach { entry: Object2ObjectMap.Entry<HistoryPair?, Message> ->
                Bukkit.getOnlinePlayers()
                    .forEach { target: Player -> target.sendMessage(entry.value.message) }
            }
    }

    companion object {
        @Getter
        private val instance = ChatHistoryService()
    }
}
