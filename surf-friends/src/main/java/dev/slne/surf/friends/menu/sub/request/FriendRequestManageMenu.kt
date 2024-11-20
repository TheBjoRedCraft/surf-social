package dev.slne.surf.friends.menu.sub.request

import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.stefvanschie.inventoryframework.font.util.Font
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.github.stefvanschie.inventoryframework.pane.component.Label
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.SurfFriendsPlugin
import dev.slne.surf.friends.listener.util.ItemBuilder
import dev.slne.surf.friends.listener.util.PluginColor
import dev.slne.surf.friends.menu.FriendMenu
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

class FriendRequestManageMenu(name: String) : FriendMenu(5, "Anfrage von $name") {
    init {
        val header = OutlinePane(0, 0, 9, 1, Pane.Priority.LOW)
        val footer = OutlinePane(0, 4, 9, 1, Pane.Priority.LOW)
        val midPane = OutlinePane(4, 2, 1, 1)
        val back = StaticPane(0, 4, 9, 1, Pane.Priority.HIGH)

        val accept = Label(1, 2, 1, 1, Pane.Priority.NORMAL, Font.OAK_PLANKS)
        val deny = Label(7, 2, 1, 1, Pane.Priority.NORMAL, Font.OAK_PLANKS)

        val target = ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(name)
            .setName(Component.text(name).color(PluginColor.BLUE_LIGHT)).build()

        header.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        header.setRepeat(true)

        footer.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        footer.setRepeat(true)

        accept.setText("+") { c: Char?, stack: ItemStack ->
            val builder = ItemBuilder(stack)
            builder.setName(Component.text("Akzeptieren"))
            builder.addLoreLine(
                Component.text(
                    "Freundschaftsanfrage akzeptieren",
                    PluginColor.DARK_GRAY
                ).decoration(TextDecoration.ITALIC, false)
            )
            builder.addLoreLine(
                Component.text(" ", PluginColor.LIGHT_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            )
            builder.addLoreLine(
                Component.text(
                    "Hier kannst du diese",
                    PluginColor.LIGHT_GRAY
                ).decoration(TextDecoration.ITALIC, false)
            )
            builder.addLoreLine(
                Component.text(
                    "Freundschaftsanfrage akzeptieren.",
                    PluginColor.LIGHT_GRAY
                ).decoration(TextDecoration.ITALIC, false)
            )
            this.build(builder)
        }

        deny.setText("X") { c: Char?, stack: ItemStack ->
            val builder = ItemBuilder(stack)
            builder.setName(Component.text("Ablehnen"))
            builder.addLoreLine(
                Component.text(
                    "Freundschaftsanfrage ablehnen",
                    PluginColor.DARK_GRAY
                ).decoration(TextDecoration.ITALIC, false)
            )
            builder.addLoreLine(
                Component.text(" ", PluginColor.LIGHT_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            )
            builder.addLoreLine(
                Component.text(
                    "Hier kannst du diese",
                    PluginColor.LIGHT_GRAY
                ).decoration(TextDecoration.ITALIC, false)
            )
            builder.addLoreLine(
                Component.text(
                    "Freundschaftsanfrage ablehnen.",
                    PluginColor.LIGHT_GRAY
                ).decoration(TextDecoration.ITALIC, false)
            )
            this.build(builder)
        }

        accept.setOnClick {
            SurfFriendsPlugin.instance.launch {
                FriendManager.acceptFriendRequest(
                    it.whoClicked.uniqueId,
                    Bukkit.getOfflinePlayer(name).uniqueId
                )
            }

            FriendRequestsMenu(it.whoClicked.uniqueId).show(it.whoClicked)
        }

        deny.setOnClick {
            SurfFriendsPlugin.instance.launch {
                FriendManager.denyFriendRequest(
                    it.whoClicked.uniqueId,
                    Bukkit.getOfflinePlayer(name).uniqueId
                )
            }

            FriendRequestsMenu(it.whoClicked.uniqueId).show(it.whoClicked)
        }

        midPane.addItem(GuiItem(target))

        back.addItem(
            build(
                ItemBuilder(Material.BARRIER).setName(
                    Component.text("Zurück").color(PluginColor.RED)
                )
            ) { event: InventoryClickEvent? ->
                if(event == null) {
                    return@build
                }

                FriendRequestsMenu(
                    event.whoClicked.uniqueId
                ).show(event.whoClicked)
            }, 4, 0
        )

        addPane(header)
        addPane(footer)
        addPane(accept)
        addPane(midPane)
        addPane(deny)
        addPane(back)


        setOnGlobalClick {
            it.isCancelled =
                true
        }
        setOnGlobalDrag {
            it.isCancelled =
                true
        }
    }
}
