package dev.slne.surf.friends.menu

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import com.github.stefvanschie.inventoryframework.pane.component.PagingButtons
import dev.slne.surf.friends.listener.util.PluginColor
import dev.slne.surf.friends.listener.util.buildItem
import dev.slne.surf.friends.plugin
import net.kyori.adventure.text.Component
import org.bukkit.Material

val backItem = buildItem(Material.BARRIER) {
    setName(Component.text("Zurück", PluginColor.RED))
}

val pageBack = buildItem(Material.RED_DYE) {
    setName(Component.text("Vorherige Seite", PluginColor.RED))
}

val pageForward = buildItem(Material.LIME_DYE) {
    setName(Component.text("Nächste Seite", PluginColor.LIGHT_GREEN))
}

fun getPagesButtons(paginatedPane: PaginatedPane, length: Int = 9): PagingButtons {
    val buttons = PagingButtons(length, paginatedPane, plugin)

    buttons.setForwardButton(GuiItem(pageForward, plugin))
    buttons.setBackwardButton(GuiItem(pageBack, plugin))
    
    return buttons
}