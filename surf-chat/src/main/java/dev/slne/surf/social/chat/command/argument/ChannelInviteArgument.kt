package dev.slne.surf.social.chat.command.argument

import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentInfoParser
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.provider.ChannelProvider
import org.bukkit.command.CommandSender

class ChannelInviteArgument(nodeName: String) :
    CustomArgument<Channel, String>(
        StringArgument(nodeName),
        CustomArgumentInfoParser { info: CustomArgumentInfo<String> ->
            val channel: Channel = Channel.getChannel(info.input()) ?: throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Unknown channel: ").appendArgInput())

            if (!channel.hasInvite(info.sender())) {
                throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Unknown channel: ").appendArgInput())
            } else {
                return@CustomArgumentInfoParser channel
            }
        }) {
    init {
        this.replaceSuggestions(ArgumentSuggestions.strings { info: SuggestionInfo<CommandSender> ->
              ChannelProvider.instance.channels.values.stream()
                .filter { channel: Channel -> channel.hasInvite(info.sender()) }
                .map { obj: Channel -> obj.name }
                .toArray { arrayOfNulls<String>(it) }
            }
        )
    }
}