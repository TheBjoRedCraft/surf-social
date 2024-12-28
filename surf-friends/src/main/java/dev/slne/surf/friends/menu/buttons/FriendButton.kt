package dev.slne.surf.friends.menu.buttons

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import dev.slne.surf.friends.listener.util.buildItem
import dev.slne.surf.friends.menu.sub.friend.FriendSingleMenu
import dev.slne.surf.friends.plugin
import org.bukkit.Material
import org.bukkit.OfflinePlayer

class FriendButton(private val offlinePlayer: OfflinePlayer) :
    GuiItem(buildItem(Material.PLAYER_HEAD) {
        setSkullOwner(offlinePlayer.name)
        setName(offlinePlayer.name ?: "Unbekannt")
    }, plugin) {
    init {
        setAction { event ->
            FriendSingleMenu(offlinePlayer.name ?: "Unbekannt").show(event.whoClicked)
        }
    }
}