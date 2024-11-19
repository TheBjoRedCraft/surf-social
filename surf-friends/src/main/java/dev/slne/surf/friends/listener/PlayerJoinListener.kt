package dev.slne.surf.friends.listener

import dev.slne.surf.friends.FriendData
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.SurfFriendsPlugin
import dev.slne.surf.friends.listener.util.PluginColor
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.function.Consumer

class PlayerJoinListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        FriendManager.Companion.loadFriendDataAsync(player.uniqueId)
            .thenAccept { friendData: FriendData? ->
                FriendManager.instance.cache.put(player.uniqueId, friendData)
                player.sendMessage(
                    SurfFriendsPlugin.prefix
                        .append(Component.text("Willkommen zurück!"))
                )
                player.sendMessage(
                    SurfFriendsPlugin.prefix.append(Component.text("Du hast noch "))
                        .append(
                            Component.text(friendData!!.friendRequests!!.size, PluginColor.GOLD)
                        ).append(Component.text(" Freundschaftsanfragen offen."))
                )
            }
    }
}
