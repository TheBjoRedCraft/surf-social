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
            val channel = args.getUnchecked<Channel>("channel") ?: return@PlayerCommandExecutor

            if (channel.closed && !channel.hasInvite(player)) {
                SurfChat.send(player, MessageBuilder().error("Der Nachrichtenkanal ist privat."))
                return@PlayerCommandExecutor
            }

            if (Channel.getChannelO(player) != null) {
                SurfChat.send(player, MessageBuilder().error("Du bist bereits in einem Nachrichtenkanal."))
                return@PlayerCommandExecutor
            }

            channel.join(player)
            SurfChat.send(player, MessageBuilder().primary("Du bist dem Nachrichtenkanal ").info(channel.name).success(" beigetreten."))
        })
    }
}
