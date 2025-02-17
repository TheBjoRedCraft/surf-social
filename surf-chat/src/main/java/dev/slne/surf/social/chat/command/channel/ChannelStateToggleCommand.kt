package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.entity.Player

class ChannelStateToggleCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        executesPlayer(PlayerCommandExecutor { player: Player, _: CommandArguments? ->
            val channel: Channel? = Channel.getChannel(player)

            if (channel == null) {
                SurfChat.send(player, MessageBuilder().error("Du bist in keinem Nachrichtenkanal."))
                return@PlayerCommandExecutor
            }

            if (!channel.isOwner(player)) {
                SurfChat.send(player, MessageBuilder().error("Du bist nicht der Besitzer des Nachrichtenkanals."))
                return@PlayerCommandExecutor
            }

            if (channel.closed) {
                channel.closed = false
                SurfChat.send(player, MessageBuilder().primary("Der Nachrichtenkanal ").info(channel.name).primary(" ist nun ").success("öffentlich."))
            } else {
                channel.closed = true
                SurfChat.send(player, MessageBuilder().primary("Der Nachrichtenkanal ").info(channel.name).primary(" ist nun ").error("privat."))
            }
        })
    }
}
