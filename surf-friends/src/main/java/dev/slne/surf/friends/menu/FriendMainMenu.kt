package dev.slne.surf.friends.menu

import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.Pane
import dev.slne.surf.friends.listener.util.ItemBuilder
import dev.slne.surf.friends.listener.util.PluginColor
import dev.slne.surf.friends.menu.sub.friend.FriendFriendsMenu
import dev.slne.surf.friends.menu.sub.request.FriendRequestsMenu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemFlag

class FriendMainMenu : FriendMenu(5, "Freunde") {
    init {
        val header = OutlinePane(0, 0, 9, 1, Pane.Priority.LOW)
        val footer = OutlinePane(0, 4, 9, 1, Pane.Priority.LOW)

        val flPane = OutlinePane(2, 2, 1, 1)
        val frPane = OutlinePane(6, 2, 1, 1)

        val friendList = ItemBuilder(Material.ENDER_PEARL)
            .setName(Component.text("Freunde").color(PluginColor.BLUE_LIGHT))
            .addLoreLine(
                Component.text("Freundesliste", PluginColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            )
            .addLoreLine(
                Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false)
            )
            .addLoreLine(
                Component.text(
                    "Hier findest du eine Auflistung aller",
                    PluginColor.LIGHT_GRAY
                ).decoration(TextDecoration.ITALIC, false)
            )
            .addLoreLine(
                Component.text("Freunde von dir.", PluginColor.LIGHT_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            )
            .addLoreLine(
                Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false)
            )
            .addLoreLine(
                Component.text("Du kannst diesen hier nachjoinen", PluginColor.LIGHT_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            )
            .addLoreLine(
                Component.text("oder sie entfernen.", PluginColor.LIGHT_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            )
            .build()
        val friendRequests = ItemBuilder(Material.PAPER)
            .setName(Component.text("Freundschaftsanfragen").color(PluginColor.BLUE_LIGHT))
            .addLoreLine(
                Component.text("Freundschaftsanfragen", PluginColor.DARK_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            )
            .addLoreLine(
                Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false)
            )
            .addLoreLine(
                Component.text("Hier findest du eine Auflistung", PluginColor.LIGHT_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            )
            .addLoreLine(
                Component.text(
                    "aller offenen Freundschaftsanfragen.",
                    PluginColor.LIGHT_GRAY
                ).decoration(TextDecoration.ITALIC, false)
            )
            .addLoreLine(
                Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false)
            )
            .addLoreLine(
                Component.text("Du kannst diese direkt hier", PluginColor.LIGHT_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            )
            .addLoreLine(
                Component.text("annehmen oder akzeptieren.", PluginColor.LIGHT_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            )
            .build()

        header.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        header.setRepeat(true)

        footer.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        footer.setRepeat(true)

        flPane.addItem(
            build(
                friendList!!
            ) { event: InventoryClickEvent? ->
                FriendFriendsMenu(
                    event!!.whoClicked.uniqueId
                ).show(event.whoClicked)
            })

        frPane.addItem(
            build(
                friendRequests!!
            ) { event: InventoryClickEvent? ->
                FriendRequestsMenu(
                    event!!.whoClicked.uniqueId
                ).show(event.whoClicked)
            })


        addPane(header)
        addPane(footer)
        addPane(flPane)
        addPane(frPane)


        setOnGlobalClick { event: InventoryClickEvent ->
            event.isCancelled =
                true
        }
        setOnGlobalDrag { event: InventoryDragEvent ->
            event.isCancelled =
                true
        }
    }
}
