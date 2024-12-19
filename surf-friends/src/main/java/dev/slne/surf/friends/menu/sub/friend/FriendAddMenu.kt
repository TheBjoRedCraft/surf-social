package dev.slne.surf.friends.menu.sub.friend

import com.github.shynixn.mccoroutine.bukkit.launch

import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.listener.util.ItemBuilder
import dev.slne.surf.friends.listener.util.PluginColor
import dev.slne.surf.friends.plugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

import net.wesjd.anvilgui.AnvilGUI
import net.wesjd.anvilgui.AnvilGUI.Builder

import org.bukkit.Bukkit
import org.bukkit.Material

class FriendAddMenu() : Builder() {
    init {
        plugin(plugin)
        title("Spielername eingeben")
        itemLeft(ItemBuilder(Material.PAPER)
            .setName(Component.text("Bitte gebe den Spielernamen ein.", PluginColor.GOLD).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text("Um eine Freundschaftsanfrage", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text("zu senden, gebe oben in das", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text("Eingabefeld den Namen der", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text("Zielperson ein.", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text(" ").decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text("Sobald du die Aktion bestätigst,", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .addLoreLine(Component.text("wird die Anfrage gesendet.", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            .build())
        text("Spielername")

        onClick { _, state ->
            if(state.text.isEmpty() || state.text.isBlank() || state.text.lowercase() == "spielername" || state.text.length > 16) {
                return@onClick listOf()
            }

            if(state.text.lowercase() == state.player.name.lowercase()) {
                return@onClick listOf()
            }

            plugin.launch {
                FriendManager.sendFriendRequest(state.player.uniqueId, Bukkit.getOfflinePlayer(state.text).uniqueId)

                FriendFriendsMenu(FriendManager.getFriends(state.player.uniqueId)).show(state.player)
            }

            return@onClick listOf(AnvilGUI.ResponseAction.close())
        }
    }
}
