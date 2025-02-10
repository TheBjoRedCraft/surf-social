package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument;
import dev.slne.surf.social.chat.object.Channel;
import org.bukkit.OfflinePlayer;

public class ChannelDemoteCommand extends CommandAPICommand {

  public ChannelDemoteCommand(String commandName) {
    super(commandName);

    withArguments(new ChannelMembersArgument("player"));
    executesPlayer((player, args) -> {
      Channel channel = Channel.getChannel(player);
      OfflinePlayer target = args.getUnchecked("player");

      if(channel == null) {
        return;
      }

      if(!channel.isOwner(player)) {
        return;
      }

      channel.demote(target);
    });
  }
}
