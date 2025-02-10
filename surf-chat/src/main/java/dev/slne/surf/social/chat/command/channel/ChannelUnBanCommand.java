package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.slne.surf.social.chat.command.argument.ChannelModerateArgument;
import dev.slne.surf.social.chat.object.Channel;
import org.bukkit.OfflinePlayer;

public class ChannelUnBanCommand extends CommandAPICommand {

  public ChannelUnBanCommand(String commandName) {
    super(commandName);

    withRequirement((sender) -> {
      Channel channel = Channel.getChannel(sender);

      if(channel == null) {
        return false;
      }

      return channel.isModerator(sender);
    });
    withArguments(new OfflinePlayerArgument("player"));
    executesPlayer((player, args) -> {
      OfflinePlayer target = args.getUnchecked("player");
      Channel channel = Channel.getChannel(player);

      if(channel == null) {
        return;
      }

      if(!channel.isModerator(player)) {
        return;
      }

      channel.unban(target);
    });
  }
}
