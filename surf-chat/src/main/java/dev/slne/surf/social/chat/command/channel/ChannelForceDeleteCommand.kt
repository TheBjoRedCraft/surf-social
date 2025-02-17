package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.argument.ChannelArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.entity.Player

class ChannelForceDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.channel.forceDelete")
        withArguments(ChannelArgument("channel"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val channel = args.getUnchecked<Channel>("channel") ?: return@PlayerCommandExecutor

            if (!channel.delete()) {
                SurfChat.send(player, MessageBuilder().error("Der Nachrichtenkanal konnte nicht gelöscht werden."))
                return@PlayerCommandExecutor
            }

            SurfChat.send(player, MessageBuilder().primary("Der Nachrichtenkanal ").info(channel.name).error(" wurde gelöscht."))
        })
    }
}
