package dev.slne.surf.social.friend.util

import com.velocitypowered.api.command.CommandSource
import com.velocitypowered.api.proxy.Player
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration

import kotlin.math.ceil
import kotlin.math.min

class PageableMessageBuilder {
    private val lines: ObjectList<Component> = ObjectArrayList()
    private var pageCommand = "An error occurred while trying to display the page."

    fun addLine(line: Component): PageableMessageBuilder {
        lines.add(line)
        return this
    }

    fun setPageCommand(command: String): PageableMessageBuilder {
        this.pageCommand = command
        return this
    }

    fun send(sender: CommandSource, page: Int) {
        val linesPerPage = 10
        val totalPages = ceil(lines.size.toDouble() / linesPerPage).toInt()
        val start = (page - 1) * linesPerPage
        val end: Int = min(start + linesPerPage, lines.size)

        if (page < 1 || page > totalPages) {
            sender.sendMessage(Component.text("Seite $page existiert nicht.", NamedTextColor.RED))
            return
        }

        var message: Component = Component.text("==== Seite $page von $totalPages ====", Colors.VARIABLE_KEY).decorate(TextDecoration.BOLD).append(Component.newline())
        val navigation = this.getComponent(page, totalPages)

        for (i in start..<end) {
            message = message.append(lines[i]).append(Component.newline()).decoration(TextDecoration.BOLD, false)
        }

        if (navigation != Component.empty()) {
            message = message.append(navigation)
        }

        sender.sendMessage(Component.newline().append(message))
    }

    private fun getComponent(page: Int, totalPages: Int): Component {
        var navigation: Component = Component.empty()

        if (page > 1) {
            navigation = navigation.append(Component.text("[<< Zurück] ", Colors.SUCCESS).clickEvent(ClickEvent.runCommand(pageCommand.replace("%page%", (page - 1).toString()))))
        }

        if (page < totalPages) {
            navigation = navigation.append(Component.text("[Weiter >>]", Colors.SUCCESS).clickEvent(ClickEvent.runCommand(pageCommand.replace("%page%", (page + 1).toString()))))
        }

        return navigation
    }
}