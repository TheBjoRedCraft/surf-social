package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.entity.Player

class ChannelLeaveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments? ->
            val channel: Channel = Channel.Companion.getChannel(player)
            if (channel == null) {
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().error("Du bist in keinem Nachrichtenkanal.")
                )
                return@executesPlayer
            }

            channel.leave(player)
            SurfChat.Companion.send(
                player,
                MessageBuilder().primary("Du hast den Nachrichtenkanal ").info(channel.name)
                    .error(" verlassen.")
            )
        })
    }
}
