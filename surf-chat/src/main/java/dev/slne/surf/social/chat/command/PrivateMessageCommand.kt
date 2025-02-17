package dev.slne.surf.social.chat.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.external.BasicPunishApi
import dev.slne.surf.social.chat.`object`.ChatUser
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.entity.Player

class PrivateMessageCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(PlayerArgument("player"))
        withArguments(GreedyStringArgument("message"))
        withAliases("tell", "w", "pm", "dm")
        withPermission("surf.chat.command.private-message")
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val target = args.getUnchecked<Player>("player") ?: return@PlayerCommandExecutor
            val message = args.getUnchecked<String>("message") ?: return@PlayerCommandExecutor

            val targetUser: ChatUser = ChatUser.getUser(target.uniqueId)


            if (BasicPunishApi.isMuted(player)) {
                SurfChat.send(player, MessageBuilder().error("Du bist gemuted und kannst nicht schreiben."))
                return@PlayerCommandExecutor
            }

            if (targetUser.toggledPM) {
                SurfChat.send(player, MessageBuilder().error("Der Spieler hat private Nachrichten deaktiviert."))
                return@PlayerCommandExecutor
            }


            SurfChat.send(target, MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ").variableValue(player.name).darkSpacer(" ->").variableValue(" Dich: ").white(message))
            SurfChat.send(player, MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ").variableValue("Du").darkSpacer(" -> ").variableValue(player.name + ": ").white(message))
        })
    }
}
