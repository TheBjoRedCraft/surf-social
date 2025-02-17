package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.argument.ChannelArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.entity.Player

class ChannelJoinCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelArgument("channel"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val channel = args.getUnchecked<Channel>("channel")
            if (channel.isClosed() && !channel!!.hasInvite(player)) {
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().error("Der Nachrichtenkanal ist privat.")
                )
                return@executesPlayer
            }

            if (Channel.Companion.getChannelO(player) != null) {
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().error("Du bist bereits in einem Nachrichtenkanal.")
                )
                return@executesPlayer
            }

            channel!!.join(player)
            SurfChat.Companion.send(
                player,
                MessageBuilder().primary("Du bist dem Nachrichtenkanal ").info(channel.name)
                    .success(" beigetreten.")
            )
        })
    }
}
