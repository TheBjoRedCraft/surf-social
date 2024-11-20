package dev.slne.surf.friends.listener

import dev.slne.surf.friends.FriendManager
import kotlinx.coroutines.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@DelicateCoroutinesApi
class PlayerQuitListener : Listener {

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player

        GlobalScope.launch {
            FriendManager.instance.saveFriendData(player.uniqueId)
            FriendManager.instance.cache.invalidate(player.uniqueId)
        }
    }
}
