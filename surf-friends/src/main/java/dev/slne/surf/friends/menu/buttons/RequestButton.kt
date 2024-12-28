package dev.slne.surf.friends.menu.buttons

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import dev.slne.surf.friends.listener.util.buildItem
import dev.slne.surf.friends.menu.sub.request.FriendRequestManageMenu
import dev.slne.surf.friends.plugin
import org.bukkit.Material
import org.bukkit.OfflinePlayer

class RequestButton(private val offlinePlayer: OfflinePlayer) :
    GuiItem(buildItem(Material.PLAYER_HEAD) {
        setSkullOwner(offlinePlayer.name)
        setName(offlinePlayer.name ?: "Unbekannt")
    }, plugin) {
    init {
        setAction { event ->
            FriendRequestManageMenu(offlinePlayer.name ?: "Unbekannt").show(event.whoClicked)
        }
    }
}