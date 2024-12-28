package dev.slne.surf.friends.listener

import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.listener.util.PluginColor
import dev.slne.surf.friends.prefix
import kotlinx.coroutines.DelicateCoroutinesApi
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@DelicateCoroutinesApi
object PlayerJoinListener : Listener {

    @EventHandler
    suspend fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val friendData = FriendManager.cache.get(player.uniqueId) ?: FriendManager.loadFriendData(player.uniqueId);

        FriendManager.cache.put(player.uniqueId, friendData)

        player.sendMessage(prefix.append(Component.text("Willkommen zurück!")))
        player.sendMessage(
            prefix.append(Component.text("Du hast noch "))
                .append(Component.text(friendData.friendRequests.size, PluginColor.GOLD))
                .append(Component.text(" Freundschaftsanfragen offen."))
        )
    }
}
