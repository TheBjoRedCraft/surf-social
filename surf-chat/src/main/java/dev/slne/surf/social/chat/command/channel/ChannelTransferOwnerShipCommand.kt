package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class ChannelTransferOwnerShipCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelMembersArgument("member"))
        withOptionalArguments(StringArgument("confirm"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            val channel: Channel = Channel.Companion.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("member")
            val confirm = args.getOrDefaultUnchecked("confirm", "")

            if (channel == null) {
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().error("Du bist in keinem Nachrichtenkanal.")
                )
                return@executesPlayer
            }

            if (!channel.isOwner(player)) {
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().error("Du bist nicht der Besitzer des Nachrichtenkanals.")
                )
                return@executesPlayer
            }

            if (!confirm.equals("confirm", ignoreCase = true) && !confirm.equals(
                    "yes",
                    ignoreCase = true
                ) && !confirm.equals("true", ignoreCase = true) && !confirm.equals(
                    "ja",
                    ignoreCase = true
                )
            ) {
                SurfChat.Companion.send(
                    player, MessageBuilder().error("Bitte bestätige den Vorgang.").command(
                        MessageBuilder().darkSpacer(" [").info("Bestätigen").darkSpacer("]"),
                        MessageBuilder().info("Klicke hier, um den Vorgang zu bestätigen."),
                        "/channel transferOwnership " + target!!.name + " confirm"
                    )
                )
                return@executesPlayer
            }

            channel.unregister(channel.owner.uniqueId)

            channel.moderators.add(channel.owner)
            channel.owner = target
            channel.members.remove(target)

            channel.register()

            SurfChat.Companion.send(
                player,
                MessageBuilder().primary("Du hast den Besitzer des Nachrichtenkanals an ").info(
                    target!!.name!!
                ).success(" übergeben.")
            )
            SurfChat.Companion.send(
                target,
                MessageBuilder().primary("Du wurdest zum Besitzer des Nachrichtenkanals ")
                    .info(channel.name).success(" ernannt.")
            )
        })
    }
}
