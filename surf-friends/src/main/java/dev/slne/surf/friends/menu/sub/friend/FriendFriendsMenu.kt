package dev.slne.surf.friends.menu.sub.friend

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.Pane
import com.github.stefvanschie.inventoryframework.pane.StaticPane
import dev.slne.surf.friends.listener.util.ItemBuilder
import dev.slne.surf.friends.listener.util.buildGuiItem
import dev.slne.surf.friends.menu.FriendMainMenu
import dev.slne.surf.friends.menu.FriendMenu
import dev.slne.surf.friends.menu.backItem
import dev.slne.surf.friends.menu.buttons.FriendButton
import dev.slne.surf.friends.menu.getPagesButtons
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Bukkit
import org.bukkit.Material
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

        pages.populateWithGuiItems(getFriendItems(friends))

        navigation.addItem(buildGuiItem(backItem) { event ->
            FriendMainMenu().show(
                event.whoClicked
            )
        }, 4, 0)

        addPane(header)
        addPane(footer)
        addPane(navigation)
        addPane(getPagesButtons(pages))
        addPane(pages)

        setOnGlobalClick {
            it.isCancelled = true
        }

        setOnGlobalDrag {
            it.isCancelled = true
        }
    }

    private fun getFriendItems(friends: ObjectList<UUID>): ObjectList<GuiItem> {
        val stacks: ObjectList<GuiItem> = ObjectArrayList()

        friends.forEach { friend ->
            stacks.add(FriendButton(Bukkit.getOfflinePlayer(friend)))
        }

        return stacks
    }
}
