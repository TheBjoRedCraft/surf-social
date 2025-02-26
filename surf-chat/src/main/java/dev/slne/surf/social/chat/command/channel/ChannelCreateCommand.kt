package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.playerExecutor
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
        playerExecutor { player, args ->
            if (Channel.getChannel(player) != null) {
                SurfChat.send(player, MessageBuilder().error("Du bist bereits in einem Nachrichtenkanal."))
                return@playerExecutor
            }

            val name = args.getUnchecked<String>("name") ?: return@playerExecutor
            val description = args.getOrDefaultUnchecked("description", "???")
            val channel = Channel(player, ObjectArraySet(), ObjectArraySet(), ObjectArraySet(), ObjectArraySet(), name, description, false)

            if (ChannelProvider.exists(name)) {
                SurfChat.send(player, MessageBuilder().error("Der Nachrichtenkanal ").info(name).error(" existiert bereits."))
                return@playerExecutor
            }

            channel.register()
            SurfChat.send(player, MessageBuilder().primary("Du hast den Nachrichtenkanal ").info(channel.name).success(" erstellt."))
        }
    }
}
