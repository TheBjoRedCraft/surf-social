package dev.slne.surf.friends.menu.sub.friend

import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.listener.util.ItemBuilder
import dev.slne.surf.friends.listener.util.PluginColor
import dev.slne.surf.friends.menu.FriendMainMenu
import dev.slne.surf.friends.menu.FriendMenu
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack
import java.util.*

class FriendFriendsMenu(player: UUID) : FriendMenu(5, "Deine Freunde") {
    init {
        val header = OutlinePane(0, 0, 9, 1, Pane.Priority.LOW)
        val footer = OutlinePane(0, 4, 9, 1, Pane.Priority.LOW)
        val pages = PaginatedPane(1, 1, 9, 3, Pane.Priority.HIGH)
        val navigation = StaticPane(0, 4, 9, 1, Pane.Priority.HIGH)

        header.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        header.setRepeat(true)

        footer.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        footer.setRepeat(true)

        pages.populateWithItemStacks(this.getFriendItems(player))

        pages.setOnClick { event: InventoryClickEvent ->
            if (event.currentItem == null) {
                return@setOnClick
            }
            val meta = event.currentItem!!.itemMeta
            FriendSingleMenu(meta.displayName).show(event.whoClicked)
        }

        navigation.addItem(
            build(
                ItemBuilder(Material.RED_DYE).setName(
                    Component.text("Vorherige Seite").color(PluginColor.RED)
                )
            ) { event: InventoryClickEvent? ->
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
            ) { event: InventoryClickEvent? ->
                if (pages.page < pages.pages - 1) {
                    pages.page = pages.page + 1
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
                FriendMainMenu().show(
                    event!!.whoClicked
                )
            }, 4, 0
        )


        addPane(header)
        addPane(footer)
        addPane(navigation)
        addPane(pages)


        setOnGlobalClick { event: InventoryClickEvent ->
            event.isCancelled =
                true
        }
        setOnGlobalDrag { event: InventoryDragEvent ->
            event.isCancelled =
                true
        }
    }


    private fun getFriendItems(player: UUID): ObjectList<ItemStack?> {
        val stacks: ObjectList<ItemStack?> = ObjectArrayList()
        val friends = FriendManager.instance.getFriends(player)

        if (friends != null) {
            for (friend in friends) {
                val offlinePlayer = Bukkit.getOfflinePlayer(friend)

                stacks.add(
                    ItemBuilder(Material.PLAYER_HEAD).setName(offlinePlayer.name!!)
                        .setSkullOwner(offlinePlayer.name).build()
                )
            }
        }

        return stacks
    }
}
