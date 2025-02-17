package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.provider.ChannelProvider
import dev.slne.surf.social.chat.util.MessageBuilder
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import org.bukkit.entity.Player

class ChannelCreateCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(TextArgument("name"))
        withOptionalArguments(TextArgument("description"))
        executesPlayer(PlayerCommandExecutor { player: Player, args: CommandArguments ->
            if (Channel.Companion.getChannel(player) != null) {
                SurfChat.Companion.send(
                    player,
                    MessageBuilder().error("Du bist bereits in einem Nachrichtenkanal.")
                )
                return@executesPlayer
            }
            val name = args.getUnchecked<String>("name")
            val description = args.getOrDefaultUnchecked("description", "???")
            val channel = Channel.builder()
                .name(name)
                .description(description)
                .members(ObjectArraySet())
                .invites(ObjectArraySet())
                .closed(true)
                .owner(player)
                .build()

            if (ChannelProvider.getInstance().exists(name)) {
                SurfChat.Companion.send(
                    player, MessageBuilder().error("Der Nachrichtenkanal ").info(
                        name!!
                    ).error(" existiert bereits.")
                )
                return@executesPlayer
            }

            channel.register()
            SurfChat.Companion.send(
                player,
                MessageBuilder().primary("Du hast den Nachrichtenkanal ").info(channel.name)
                    .success(" erstellt.")
            )
        })
    }
}
