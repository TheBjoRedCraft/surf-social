package dev.slne.surf.social.chat.command.argument;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.provider.ChannelProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class ChannelMembersArgument extends CustomArgument<OfflinePlayer, String> {
  public ChannelMembersArgument(String nodeName) {
    super(new StringArgument(nodeName), info -> {
      OfflinePlayer player = Bukkit.getOfflinePlayer(info.input());
      Channel channel = Channel.getChannelO(player);

      if(channel == null) {
        throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Du bist in keinem Kanal, oder dieser ist invalid."));
      }

      if(!channel.isMember(player) && !channel.isOwnerO(player) && !channel.isModeratorO(player)) {
        throw CustomArgumentException.fromMessageBuilder(new MessageBuilder("Player is not a member of the channel: ").appendArgInput());
      }

      return player;
    });

    this.replaceSuggestions(ArgumentSuggestions.strings(info -> {
      try {
        return ChannelProvider.getInstance().getChannels().values()
            .stream()
            .filter(channel ->  channel.isMember(info.sender()) || channel.isOwner(info.sender())).findFirst().orElseThrow(() -> CustomArgumentException.fromString("You are not a member of any channel."))
            .getMembers()
            .stream()
            .filter(player -> player != info.sender())
            .map(OfflinePlayer::getName)
            .toArray(String[]::new);
      } catch (CustomArgumentException e) {
        info.sender().sendMessage(Component.text("You are not a member of any channel.").color(NamedTextColor.RED));
        return new String[0];
      }
    }));
  }
}