package dev.slne.surf.friends.menu

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import dev.slne.surf.friends.listener.util.ItemBuilder
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

open class FriendAnvilMenu(title: String) {
    fun build(builder: ItemBuilder, action: Consumer<InventoryClickEvent?>?): GuiItem {
        return GuiItem(builder.build(), action)
    }

    fun build(stack: ItemStack, action: Consumer<InventoryClickEvent?>?): GuiItem {
        return GuiItem(stack, action)
    }

    fun build(builder: ItemBuilder): GuiItem {
        return GuiItem(builder.build())
    }
}
