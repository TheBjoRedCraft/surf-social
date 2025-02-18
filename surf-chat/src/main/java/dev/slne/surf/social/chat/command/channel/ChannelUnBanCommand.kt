package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class ChannelUnBanCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(OfflinePlayerArgument("player"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@PlayerCommandExecutor
            val channel: Channel? = Channel.getChannel(player)

            if (channel == null) {
                SurfChat.send(player, MessageBuilder().error("Du bist in keinem Nachrichtenkanal."))
                return@PlayerCommandExecutor
            }

            if (!channel.isModerator(player) && !channel.isOwner(player)) {
                SurfChat.send(player, MessageBuilder().error("Du bist nicht der Moderator oder Besitzer des Nachrichtenkanals."))
                return@PlayerCommandExecutor
            }

            channel.unban(target)

            SurfChat.send(player, MessageBuilder().primary("Du hast ").info(target.name ?: target.uniqueId.toString()).primary(" im Nachrichtenkanal ").info(channel.name).error(" entbannt."))
            SurfChat.send(target, MessageBuilder().primary("Du wurdest im Nachrichtenkanal ").info(channel.name).error(" entbannt."))
        })
    }
}
