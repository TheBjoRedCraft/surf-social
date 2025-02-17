package dev.slne.surf.social.chat.listener

import dev.slne.surf.social.chat.provider.ChannelProvider
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener : Listener {
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        ChatHistoryService.getInstance().clearInternalChatHistory(event.player.uniqueId)
        ChannelProvider.getInstance().handleQuit(event.player)
    }
}
