package dev.slne.surf.friends.listener

import dev.slne.surf.friends.FriendManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener : Listener {
    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        FriendManager.instance.saveFriendData(player.uniqueId).thenRun {
            FriendManager.instance.cache.invalidate(player.uniqueId)
        }.join()
    }
}
