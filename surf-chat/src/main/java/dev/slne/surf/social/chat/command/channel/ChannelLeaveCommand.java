package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.command.argument.ChannelArgument;
import dev.slne.surf.social.chat.object.Channel;

public class ChannelLeaveCommand extends CommandAPICommand {

  public ChannelLeaveCommand(String commandName) {
    super(commandName);

    executesPlayer((player, args) -> {
      Channel channel = Channel.getChannel(player);

      if(channel == null) {
        return;
      }

      channel.leave(player);
    });
  }
}
