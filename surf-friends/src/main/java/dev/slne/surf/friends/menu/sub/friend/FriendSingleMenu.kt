package dev.slne.surf.friends.menu.sub.friend

import com.github.stefvanschie.inventoryframework.font.util.Font
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.github.stefvanschie.inventoryframework.pane.component.Label
import dev.slne.surf.friends.FriendManager
import dev.slne.surf.friends.listener.util.ItemBuilder
import dev.slne.surf.friends.listener.util.PluginColor
import dev.slne.surf.friends.menu.FriendMenu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.text.SimpleDateFormat
import java.util.*

class FriendSingleMenu(name: String) : FriendMenu(5, name) {
    init {
        val header = OutlinePane(0, 0, 9, 1, Pane.Priority.LOW)
        val footer = OutlinePane(0, 4, 9, 1, Pane.Priority.LOW)
        val navigation = StaticPane(0, 4, 9, 1, Pane.Priority.HIGH)

        val mid = OutlinePane(4, 2, 1, 1)
        val right = OutlinePane(7, 2, 1, 1)

        val remove = Label(1, 2, 1, 1, Font.OAK_PLANKS)

        val offlinePlayer = Bukkit.getOfflinePlayer(name)
        val date = Date(offlinePlayer.lastSeen)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")

        header.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        header.setRepeat(true)

        footer.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        footer.setRepeat(true)

        navigation.addItem(
            build(
                ItemBuilder(Material.BARRIER).setName(
                    Component.text("Zurück").color(PluginColor.RED)
                )
            ) { event: InventoryClickEvent? ->
                FriendFriendsMenu(
                    event!!.whoClicked.uniqueId
                ).show(event.whoClicked)
            }, 4, 0
        )
        mid.addItem(
            build(
                ItemBuilder(Material.PLAYER_HEAD)
                    .setName(Component.text(name, PluginColor.BLUE_LIGHT))
                    .setSkullOwner(offlinePlayer.name)
            )
        )

        remove.setText("-") { c: Char?, stack: ItemStack ->
            val builder = ItemBuilder(stack)
            builder.setName(Component.text("Entfernen"))
            builder.addLoreLine(
                Component.text(
                    "Freund/in entfernen",
                    PluginColor.DARK_GRAY
                ).decoration(TextDecoration.ITALIC, false)
            )
            builder.addLoreLine(
                Component.text(" ", PluginColor.LIGHT_GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            )
            builder.addLoreLine(
                Component.text(
                    "Hier kannst du diesen Spieler",
                    PluginColor.LIGHT_GRAY
                ).decoration(TextDecoration.ITALIC, false)
            )
            builder.addLoreLine(
                Component.text(
                    "aus deiner Freundesliste entfernen.",
                    PluginColor.LIGHT_GRAY
                ).decoration(TextDecoration.ITALIC, false)
            )
            this.build(builder)
        }
        if (offlinePlayer.isOnline) {
            right.addItem(
                build(
                    ItemBuilder(Material.ENDER_PEARL)
                        .setName(Component.text("Nachspringen").color(PluginColor.BLUE_LIGHT))
                        .addLoreLine(
                            Component.text("Nachspringen", PluginColor.DARK_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        .addLoreLine(
                            Component.text(" ", PluginColor.LIGHT_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        .addLoreLine(
                            Component.text("Hier kannst du diesem", PluginColor.LIGHT_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        .addLoreLine(
                            Component.text("Spieler nachjoinen.", PluginColor.LIGHT_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        .addLoreLine(
                            Component.text(" ", PluginColor.LIGHT_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        .addLoreLine(
                            Component.text("Er ist aktuell auf ", PluginColor.LIGHT_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        .addLoreLine(
                            Component.text(
                                FriendManager.instance.getServer(offlinePlayer.uniqueId),
                                PluginColor.GOLD
                            ).decoration(TextDecoration.ITALIC, false)
                                .append(Component.text(" online.", PluginColor.LIGHT_GRAY))
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        .setSkullOwner(name)
                )
            )
        } else {
            right.addItem(
                build(
                    ItemBuilder(Material.ENDER_PEARL)
                        .setName(Component.text("Nachspringen").color(PluginColor.BLUE_LIGHT))
                        .addLoreLine(
                            Component.text("Nachspringen", PluginColor.DARK_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        .addLoreLine(
                            Component.text(" ", PluginColor.LIGHT_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        .addLoreLine(
                            Component.text(
                                "Dieser Spieler ist aktuell offline.",
                                PluginColor.LIGHT_GRAY
                            ).decoration(TextDecoration.ITALIC, false)
                        )
                        .addLoreLine(
                            Component.text(
                                "Du kannst ihm nicht nachspringen!",
                                PluginColor.LIGHT_GRAY
                            ).decoration(TextDecoration.ITALIC, false)
                        )
                        .addLoreLine(
                            Component.text(" ", PluginColor.LIGHT_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        .addLoreLine(
                            Component.text("Er war zuletzt am ", PluginColor.LIGHT_GRAY)
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        .addLoreLine(
                            Component.text(dateFormat.format(date), PluginColor.GOLD)
                                .decoration(TextDecoration.ITALIC, false)
                                .append(Component.text(" online.", PluginColor.LIGHT_GRAY))
                                .decoration(TextDecoration.ITALIC, false)
                        )
                        .setSkullOwner(name)
                )
            )
        }

        remove.setOnClick { event: InventoryClickEvent ->
            FriendRemoveConfirmMenu(name).show(event.whoClicked)
        }

        addPane(header)
        addPane(footer)
        addPane(navigation)
        addPane(remove)

        addPane(right)
        addPane(mid)

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
