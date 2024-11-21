package dev.slne.surf.friends.menu.sub.friend

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.SurfFriendsPlugin
import dev.slne.surf.friends.listener.util.ItemBuilder
import dev.slne.surf.friends.listener.util.PluginColor
import dev.slne.surf.friends.menu.FriendMainMenu
import dev.slne.surf.friends.menu.FriendMenu
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class FriendFriendsMenu(friends: ObjectList<UUID>) : FriendMenu(5, "Deine Freunde") {
    init {
        val header = OutlinePane(0, 0, 9, 1, Pane.Priority.LOW)
        val footer = OutlinePane(0, 4, 9, 1, Pane.Priority.LOW)
        val pages = PaginatedPane(1, 1, 9, 3, Pane.Priority.HIGH)
        val navigation = StaticPane(0, 4, 9, 1, Pane.Priority.HIGH)

        header.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        header.setRepeat(true)

        footer.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        footer.setRepeat(true)

        pages.populateWithItemStacks(getFriendItems(friends))

        pages.setOnClick {
            if (it.currentItem == null) {
                return@setOnClick
            }

            val item = it.currentItem ?: return@setOnClick

            if(item.itemMeta == null) {
                return@setOnClick
            }

            val meta = item.itemMeta

            FriendSingleMenu(meta.displayName).show(it.whoClicked)
        }

        navigation.addItem(
            build(
                ItemBuilder(Material.RED_DYE).setName(
                    Component.text("Vorherige Seite").color(PluginColor.RED)
                )
            ) {
                if (pages.page > 0) {
                    pages.page -= 1

                    update()
                }
            }, 0, 0
        )

        navigation.addItem(
            build(
                ItemBuilder(Material.LIME_DYE).setName(
                    Component.text("Nächste Seite").color(PluginColor.LIGHT_GREEN)
                )
            ) {
                if (pages.page < pages.pages - 1) {
                    pages.page += 1
                    update()
                }
            }, 8, 0
        )

        navigation.addItem(
            build(
                ItemBuilder(Material.BARRIER).setName(
                    Component.text("Zurück").color(PluginColor.RED)
                )
            ) { event: InventoryClickEvent? ->
                if(event == null) {
                    return@build
                }

                FriendMainMenu().show(
                    event.whoClicked
                )
            }, 4, 0
        )


        addPane(header)
        addPane(footer)
        addPane(navigation)
        addPane(pages)


        setOnGlobalClick {
            it.isCancelled = true
        }
        setOnGlobalDrag {
            it.isCancelled = true
        }
    }


    private fun getFriendItems(friends: ObjectList<UUID>): ObjectList<ItemStack?> {
        val stacks: ObjectList<ItemStack?> = ObjectArrayList()

        for (friend in friends) {
            val offlinePlayer = Bukkit.getOfflinePlayer(friend)

            stacks.add(ItemBuilder(Material.PLAYER_HEAD).setName(offlinePlayer.name ?: "Unbekannt").setSkullOwner(offlinePlayer.name).build())
        }

        return stacks
    }
}
