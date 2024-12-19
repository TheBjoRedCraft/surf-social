package dev.slne.surf.friends.menu.sub.friend

import com.github.stefvanschie.inventoryframework.font.util.Font
import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import com.github.stefvanschie.inventoryframework.pane.component.Label
import dev.slne.surf.friends.listener.util.ItemBuilder
import dev.slne.surf.friends.listener.util.PluginColor
import dev.slne.surf.friends.listener.util.buildGuiItem
import dev.slne.surf.friends.menu.FriendMainMenu
import dev.slne.surf.friends.menu.FriendMenu
import dev.slne.surf.friends.menu.backItem
import dev.slne.surf.friends.menu.buttons.FriendButton
import dev.slne.surf.friends.menu.getPagesButtons
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class FriendFriendsMenu(friends: ObjectList<UUID>) : FriendMenu(5, "Deine Freunde") {
    init {
        val header = OutlinePane(0, 0, 9, 1, Pane.Priority.LOW)
        val footer = OutlinePane(0, 4, 9, 1, Pane.Priority.LOW)
        val pages = PaginatedPane(1, 1, 9, 3, Pane.Priority.HIGH)
        val navigation = StaticPane(0, 4, 9, 1, Pane.Priority.HIGH)
        val addFriend = Label(4, 0, 1, 1, Pane.Priority.NORMAL, Font.OAK_PLANKS)

        header.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        header.setRepeat(true)

        footer.addItem(build(ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName("")))
        footer.setRepeat(true)

        addFriend.setText("+") { _: Char?, stack: ItemStack ->
            val builder = ItemBuilder(stack)

            builder.setName(Component.text("Freund/in hinzufügen"))
            builder.addLoreLine(Component.text("Freund/in hinzufügen", PluginColor.DARK_GRAY).decoration(TextDecoration.ITALIC, false))
            builder.addLoreLine(Component.text(" ", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            builder.addLoreLine(Component.text("Hier kannst du eine Person", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            builder.addLoreLine(Component.text("zu deiner Freundschaftsliste", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))
            builder.addLoreLine(Component.text("hinzufügen.", PluginColor.LIGHT_GRAY).decoration(TextDecoration.ITALIC, false))

            this.build(builder) {
                if (it == null) {
                    return@build
                }

                FriendAddMenu().open(it.whoClicked as Player)
            }
        }

        pages.populateWithGuiItems(getFriendItems(friends))
        navigation.addItem(buildGuiItem(backItem) { event -> FriendMainMenu().show(event.whoClicked) }, 4, 0)

        addPane(header)
        addPane(footer)
        addPane(navigation)
        addPane(getPagesButtons(pages))
        addPane(pages)
        addPane(addFriend)

        setOnGlobalClick {
            it.isCancelled = true
        }

        setOnGlobalDrag {
            it.isCancelled = true
        }
    }

    private fun getFriendItems(friends: ObjectList<UUID>): ObjectList<GuiItem> {
        val stacks: ObjectList<GuiItem> = ObjectArrayList()

        friends.forEach {
            stacks.add(FriendButton(Bukkit.getOfflinePlayer(it)))
        }

        return stacks
    }
}
