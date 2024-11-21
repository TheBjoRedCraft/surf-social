package dev.slne.surf.friends.menu.sub.friend

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.SurfFriendsPlugin
import dev.slne.surf.friends.listener.util.ItemBuilder
import dev.slne.surf.friends.listener.util.PluginColor
import dev.slne.surf.friends.menu.FriendMenu
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material

class FriendRemoveConfirmMenu(name: String) : FriendMenu(5, "Bitte bestätige.") {
    init {
        val header = OutlinePane(0, 0, 9, 1, Pane.Priority.LOW)
        val footer = OutlinePane(0, 4, 9, 1, Pane.Priority.LOW)
        val navigation = StaticPane(0, 4, 9, 1, Pane.Priority.HIGH)
        val left = OutlinePane(1, 2, 1, 1)
        val mid = OutlinePane(4, 2, 1, 1)

        val offlinePlayer = Bukkit.getOfflinePlayer(name)

        header.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        header.setRepeat(true)

        footer.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        footer.setRepeat(true)

        mid.addItem(build(
                ItemBuilder(Material.PLAYER_HEAD).setName(
                    Component.text(
                        "Möchtest du $name wirklich enfernen?"
                    )
                ).setSkullOwner(offlinePlayer.name)
            )
        )

        left.addItem(build(ItemBuilder(Material.LIME_DYE).setName(Component.text("Bestätigen", PluginColor.LIGHT_GREEN))) {
                if(it == null) {
                    return@build
                }

                SurfFriendsPlugin.instance.launch {
                    FriendManager.removeFriend(it.whoClicked.uniqueId, offlinePlayer.uniqueId)
                    FriendManager.removeFriend(offlinePlayer.uniqueId, it.whoClicked.uniqueId)
                }

                SurfFriendsPlugin.instance.launch {
                    FriendFriendsMenu(FriendManager.getFriends(it.whoClicked.uniqueId)).show(it.whoClicked)
                }
            })

        navigation.addItem(build(ItemBuilder(Material.BARRIER).setName(Component.text("Zurück", PluginColor.RED))) {
                if(it == null) {
                    return@build
                }

                if (offlinePlayer.name == null) {
                    SurfFriendsPlugin.instance.launch {
                        FriendFriendsMenu(FriendManager.getFriends(it.whoClicked.uniqueId)).show(it.whoClicked)
                    }
                } else {
                    FriendSingleMenu(offlinePlayer.name ?: "Unbekannt").show(it.whoClicked)
                }
            }, 4, 0
        )


        addPane(header)
        addPane(footer)
        addPane(navigation)
        addPane(mid)
        addPane(left)

        setOnGlobalClick {
            it.isCancelled = true
        }
        setOnGlobalDrag {
            it.isCancelled = true
        }
    }
}
