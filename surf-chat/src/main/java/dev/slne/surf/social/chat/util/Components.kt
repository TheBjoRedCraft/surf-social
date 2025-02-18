package dev.slne.surf.social.chat.util

import dev.slne.surf.social.chat.`object`.Channel
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.entity.Player

object Components {
    private val deletePerms = "surf.chat.delete"
    private val teleportPerms = "surf.chat.teleport"

    fun getDeleteComponent(player: Player, id: Int): Component {
        return if (player.hasPermission(this.deletePerms)) Component.text("[", Colors.DARK_SPACER)
            .append(Component.text("DEL", Colors.VARIABLE_KEY)).append(Component.text("] ", Colors.DARK_SPACER))
            .clickEvent(ClickEvent.runCommand("/surfchat delete $id"))
            .hoverEvent(Component.text("Nachricht löschen", PluginColor.RED)) else Component.empty()
    }

    fun getChannelComponent(channel: Channel): Component {
        return MessageBuilder().darkSpacer("[").variableKey(channel.name).darkSpacer("] ").build()
    }

    fun getTeleportComponent(player: Player, name: String): Component {
        return if (player.hasPermission(this.teleportPerms)) Component.text("[", Colors.DARK_SPACER)
            .append(Component.text("TP", Colors.VARIABLE_KEY)).append(Component.text("] ", Colors.DARK_SPACER))
            .clickEvent(ClickEvent.runCommand("/tp $name"))
            .hoverEvent(Component.text("Zum Spieler teleportieren", PluginColor.BLUE_MID)) else Component.empty()
    }
}