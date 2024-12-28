package dev.slne.surf.friends.listener

import dev.slne.surf.friends.FriendManager
import kotlinx.coroutines.DelicateCoroutinesApi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@DelicateCoroutinesApi
object PlayerQuitListener : Listener {

    @EventHandler
    suspend fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        FriendManager.saveFriendData(player.uniqueId)
    }
}
