package dev.slne.surf.social.chat.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentInfoParser
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.provider.ChannelProvider

class ChannelArgument(nodeName: String) :
    CustomArgument<Channel, String>(
        StringArgument(nodeName),
        CustomArgumentInfoParser { info: CustomArgumentInfo<String> ->
            val channel: Channel = Channel.getChannel(info.input()) ?: throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Unknown channel: ").appendArgInput())

            return@CustomArgumentInfoParser channel
        }) {
    init {
        this.replaceSuggestions(ArgumentSuggestions.strings {
                ChannelProvider.instance.channels.values.stream()
                    .map { obj: Channel -> obj.name }
                    .toArray { arrayOfNulls<String>(it) }
            }
        )
    }
}