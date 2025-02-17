package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.command.argument.ChannelArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class ChannelInfoCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withOptionalArguments(ChannelArgument("channel"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val channel =
                args.getOrDefaultUnchecked<Channel?>(
                    "channel",
                    Channel.Companion.getChannel(player)
                )
                    ?: return@executesPlayer
            player.sendMessage(createInfoMessage(channel)!!)
        })
    }

    private fun createInfoMessage(channel: Channel): Component? {
        return MessageBuilder()
            .primary("Kanalinformation: ").info(channel.name).newLine()
            .darkSpacer("   - ").variableKey("Beschreibung: ").variableValue(channel.description)
            .newLine()
            .darkSpacer("   - ").variableKey("Besitzer: ").variableValue(channel.owner.name)
            .newLine()
            .darkSpacer("   - ").variableKey("Status: ")
            .variableValue(if (channel.isClosed) "Geschlossen" else "Offen").newLine()
            .darkSpacer("   - ").variableKey("Mitglieder: ")
            .variableValue((channel.members.size + channel.moderators.size + 1).toString())
            .newLine()
            .darkSpacer("   - ").variableKey("Einladungen: ")
            .variableValue(channel.invites.size.toString()).newLine()
            .build()
    }
}
