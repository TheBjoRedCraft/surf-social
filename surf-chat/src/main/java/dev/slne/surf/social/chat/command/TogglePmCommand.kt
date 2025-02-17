package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.entity.Player

class TogglePmCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.toggle")
        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments ->
            val user: ChatUser = ChatUser.getUser(player.uniqueId)

            if (user.toggledPM) {
                user.toggledPM = false
                SurfChat.send(player, MessageBuilder().primary("Du hast privat Nachrichten ").success("aktiviert."))
            } else {
                user.toggledPM = true
                SurfChat.send(player, MessageBuilder().primary("Du hast privat Nachrichten ").error("deaktiviert."))
            }
        })
    }
}
