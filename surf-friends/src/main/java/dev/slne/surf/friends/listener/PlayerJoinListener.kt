package dev.slne.surf.friends.listener

import com.github.shynixn.mccoroutine.bukkit.launch
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.SurfFriendsPlugin
import dev.slne.surf.friends.listener.util.PluginColor
import kotlinx.coroutines.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import net.kyori.adventure.text.Component

@DelicateCoroutinesApi
class PlayerJoinListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        SurfFriendsPlugin.instance.launch {
            val friendData = FriendManager.loadFriendData(player.uniqueId)

            FriendManager.cache.put(player.uniqueId, friendData)

            player.sendMessage(
                SurfFriendsPlugin.prefix
                    .append(Component.text("Willkommen zurück!"))
            )
            player.sendMessage(
                SurfFriendsPlugin.prefix.append(Component.text("Du hast noch "))
                    .append(Component.text(friendData.friendRequests.size, PluginColor.GOLD))
                    .append(Component.text(" Freundschaftsanfragen offen."))
            )
        }
    }
}
