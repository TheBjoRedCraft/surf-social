package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument;
import dev.slne.surf.social.chat.object.Channel;
import org.bukkit.OfflinePlayer;

public class ChannelBanCommand extends CommandAPICommand {

  public ChannelBanCommand(String commandName) {
    super(commandName);

    withRequirement((sender) -> {
      Channel channel = Channel.getChannel(sender);

      if(channel == null) {
        return false;
      }

      return channel.isModerator(sender);
    });

    withArguments(new ChannelMembersArgument("player"));
    executesPlayer((player, args) -> {
      Channel channel = Channel.getChannel(player);
      OfflinePlayer target = args.getUnchecked("player");

      if(channel == null) {
        return;
      }

      if(!channel.isModerator(player)) {
        return;
      }

      channel.ban(target);
    });
  }
}
