package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.entity.Player

class PrivateMessageCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(PlayerArgument("player"))
        withArguments(GreedyStringArgument("message"))
        withAliases("tell", "w", "pm", "dm")
        withPermission("surf.chat.command.private-message")
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val target = args.getUnchecked<Player>("player")
            val message = args.getUnchecked<String>("message")

            val targetUser: ChatUser = ChatUser.Companion.getUser(target!!.uniqueId)


            if (BasicPunishApi.isMuted(player)) {
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().error("Du bist gemuted und kannst nicht schreiben.")
                )
                return@executesPlayer
            }

            if (targetUser.isToggledPM()) {
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().error("Der Spieler hat private Nachrichten deaktiviert.")
                )
                return@executesPlayer
            }


            SurfChat.Companion.send(
                target,
                MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ")
                    .variableValue(player.name).darkSpacer(" ->").variableValue(" Dich: ").white(
                        message!!
                    )
            )
            SurfChat.Companion.send(
                player,
                MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ").variableValue("Du")
                    .darkSpacer(" -> ").variableValue(player.name + ": ").white(
                        message
                    )
            )
        })
    }
}
