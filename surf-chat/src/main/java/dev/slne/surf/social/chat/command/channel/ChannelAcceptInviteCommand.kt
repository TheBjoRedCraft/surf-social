package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.argument.ChannelInviteArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.entity.Player

class ChannelAcceptInviteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelInviteArgument("channel"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val channel = args.getUnchecked<Channel>("channel")
            if (!channel!!.hasInvite(player)) {
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().primary("Du hast keine Einladung in den Nachrichtenkanal ")
                        .info(channel.name).error(" erhalten.")
                )
                return@executesPlayer
            }

            channel.acceptInvite(player)
            SurfChat.Companion.send(
                player,
                MessageBuilder().primary("Du hast die Einladung in den Nachrichtenkanal ")
                    .info(channel.name).success(" angenommen.")
            )
        })
    }
}
