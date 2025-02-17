package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.entity.Player

class TogglePmCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.toggle")
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments? ->
            val user: ChatUser = ChatUser.Companion.getUser(player.uniqueId)
            if (user.isToggledPM()) {
                user.setToggledPM(false)
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().primary("Du hast privat Nachrichten ").success("aktiviert.")
                )
            } else {
                user.setToggledPM(true)
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().primary("Du hast privat Nachrichten ").error("deaktiviert.")
                )
            }
        })
    }
}
