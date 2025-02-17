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

class ChannelBanCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelMembersArgument("player"))
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
                    MessageBuilder().primary("Du bist ").error("kein Moderator ")
                        .primary("in deinem Kanal.")
                )
                return@executesPlayer
            }

            channel.ban(target!!)

            SurfChat.Companion.send(
                player, MessageBuilder().primary("Du hast ").info(
                    target.name!!
                ).primary(" aus dem Nachrichtenkanal ").info(channel.name).error(" verbannt.")
            )
            SurfChat.Companion.send(
                target,
                MessageBuilder().primary("Du wurdest aus dem Nachrichtenkanal ").info(channel.name)
                    .error(" verbannt.")
            )
        })
    }
}
