package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import dev.slne.surf.social.chat.util.PageableMessageBuilder
import org.bukkit.entity.Player

class ChannelMembersCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withOptionalArguments(IntegerArgument("page"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val message = PageableMessageBuilder()
            val page = args.getOrDefaultUnchecked("page", 1)
            val channel: Channel = Channel.Companion.getChannelO(player)

            if (channel == null) {
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().error("Du bist in keinem Nachrichtenkanal.")
                )
                return@executesPlayer
            }

            var index = 1

            message.setPageCommand("/channel members " + channel.name + " %page%")

            message.addLine(
                MessageBuilder().variableValue("$index. ").primary(channel.owner.name)
                    .darkSpacer(" (Besitzer)").build()
            )

            for (moderator in channel.moderators) {
                index++

                message.addLine(
                    MessageBuilder().variableValue("$index. ").primary(
                        moderator.name!!
                    ).darkSpacer(" (Moderator)").build()
                )
            }

            for (member in channel.members) {
                index++

                message.addLine(
                    MessageBuilder().variableValue("$index. ").primary(
                        member.name!!
                    ).darkSpacer(" (Mitglied)").build()
                )
            }
            message.send(player, page)
        })
    }
}
