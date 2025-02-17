package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.argument.ChannelArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class ChannelMoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(OfflinePlayerArgument("player"))
        withOptionalArguments(ChannelArgument("channel"))
        withPermission("surf.chat.command.channel.move")

        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val target = args.getUnchecked<OfflinePlayer>("player")
            val channel = args.getUnchecked<Channel>("channel")

            channel!!.move(target!!, channel)

            SurfChat.Companion.send(
                player, MessageBuilder().primary("Du hast ").info(
                    target.name!!
                ).primary(" in den Nachrichtenkanal ").info(channel.name).success(" verschoben.")
            )
            SurfChat.Companion.send(
                target,
                MessageBuilder().primary("Du wurdest in den Nachrichtenkanal ").info(channel.name)
                    .success(" verschoben.")
            )
        })
    }
}
