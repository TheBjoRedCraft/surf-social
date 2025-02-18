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

object ChatHistoryService {
    private val chatHistoryCache: Cache<UUID, Object2ObjectMap<HistoryPair, Message>?> = Caffeine
            .newBuilder()
            .build<UUID, Object2ObjectMap<HistoryPair, Message>?>()

    fun clearInternalChatHistory(player: UUID) {
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

    fun insertNewMessage(player: UUID, message: Message, messageID: Int) {
        var chatHistory: Object2ObjectMap<HistoryPair, Message>? = chatHistoryCache.getIfPresent(player)

        if (chatHistory == null) {
            chatHistory = Object2ObjectOpenHashMap()
            chatHistoryCache.put(player, chatHistory)
        }

        val timestamp: Int = (System.currentTimeMillis() / 1000).toInt()
        chatHistory[HistoryPair(messageID, timestamp.toLong())] = message
    }


    fun removeMessage(player: UUID, messageID: Int) {
        val chatHistory: Object2ObjectMap<HistoryPair, Message> = chatHistoryCache.getIfPresent(player) ?: return

        val key = chatHistory.keys.stream().filter { it.messageID == messageID }.findFirst().orElse(null)

        if (key != null) {
            chatHistory.remove(key)
        }
    }

    fun resend(player: UUID) {
        val chatHistory: Object2ObjectMap<HistoryPair, Message> = chatHistoryCache.getIfPresent(player) ?: return

        Bukkit.getOnlinePlayers().forEach { online: Player ->
            var index = 0
            while (index < 100) {
                online.sendMessage(Component.empty())
                index++
            }
        }

        chatHistory.object2ObjectEntrySet()
            .stream()
            .sorted(Comparator.comparingLong<Object2ObjectMap.Entry<HistoryPair, Message>> { entry: Object2ObjectMap.Entry<HistoryPair, Message> -> entry.key.sendTime })
            .forEach { entry: Object2ObjectMap.Entry<HistoryPair?, Message> -> Bukkit.getOnlinePlayers().forEach { target: Player -> target.sendMessage(entry.value.message) } }
    }
}
