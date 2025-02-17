package dev.slne.surf.social.chat.command.argument

import dev.jorel.commandapi.SuggestionInfo
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.CustomArgument.CustomArgumentInfoParser
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.social.chat.`object`.Channel
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

class ChannelMembersArgument(nodeName: String) :
    CustomArgument<OfflinePlayer, String>(
        StringArgument(nodeName),
        CustomArgumentInfoParser<OfflinePlayer, String?> { info: CustomArgumentInfo<String?> ->
            val player = Bukkit.getOfflinePlayer(info.input())
            val channel: Channel = Channel.getChannelO(player) ?: throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Du bist in keinem Kanal, oder dieser ist invalid."))

            if (!channel.isMember(player) && !channel.isOwnerO(player) && !channel.isModeratorO(player)) {
                throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Player is not a member of the channel: ").appendArgInput())
            }

            player

        }) {
    init {
        this.replaceSuggestions(ArgumentSuggestions.strings { info: SuggestionInfo<CommandSender> ->
            val channel: Channel = Channel.getChannel(info.sender()) ?: return@strings arrayOfNulls<String>(0)
            val members: ObjectSet<String> = ObjectArraySet()
            val owner = channel.owner ?: return@strings arrayOfNulls<String>(0)

            members.addAll(channel.members.stream().map<String> { obj: OfflinePlayer -> obj.name }.toList())
            members.addAll(channel.moderators.stream().map<String> { obj: OfflinePlayer -> obj.name }.toList())
            members.add(owner.name)
            members.remove(info.sender().name)
            members.stream().toArray { arrayOfNulls<String>(it) }
            }
        )
    }
}