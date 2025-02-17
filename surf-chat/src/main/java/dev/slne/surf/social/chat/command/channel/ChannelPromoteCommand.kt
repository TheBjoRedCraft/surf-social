package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class ChannelPromoteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelMembersArgument("player"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val channel: Channel? = Channel.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@PlayerCommandExecutor

            if(channel == null) {
                SurfChat.send(player, MessageBuilder().error("Du bist in keinem Nachrichtenkanal."))
                return@PlayerCommandExecutor
            }

            if (!channel.isOwner(player)) {
                SurfChat.send(
                    player,
                    MessageBuilder().error("Du bist nicht der Besitzer des Nachrichtenkanals.")
                )
                return@PlayerCommandExecutor
            }

            channel.promote(target)

            SurfChat.send(player, MessageBuilder().primary("Du hast den Spieler ").info(target.name ?: target.uniqueId.toString()).success(" befördert."))
            SurfChat.send(target, MessageBuilder().primary("Du wurdest ").success("befördert"))
        })
    }
}
