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

class ChannelInviteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(OfflinePlayerArgument("player"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val channel: Channel = Channel.Companion.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("player")

            if (channel == null) {
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().error("Du bist in keinem Nachrichtenkanal.")
                )
                return@executesPlayer
            }

            if (!channel.isModerator(player) && !channel.isOwner(player)) {
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().error("Du bist nicht der Moderator oder Besitzer des Nachrichtenkanals.")
                )
                return@executesPlayer
            }

            channel.invite(target)

            SurfChat.Companion.send(
                player, MessageBuilder().primary("Du hast ").info(
                    target!!.name!!
                ).primary(" in den Nachrichtenkanal ").info(channel.name).success(" eingeladen.")
            )
            SurfChat.Companion.send(
                target,
                MessageBuilder().primary("Du wurdest in den Nachrichtenkanal ").info(channel.name)
                    .success(" eingeladen. ").command(
                        MessageBuilder().darkSpacer("[").success("Beitreten").darkSpacer("]"),
                        MessageBuilder().success("Klicke, um beizutreten"),
                        "/channel accept " + channel.name
                    )
            )
        })
    }
}
