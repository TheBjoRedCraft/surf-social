package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.command.argument.ChannelArgument;
import dev.slne.surf.social.chat.object.Channel;

public class ChannelForceJoinCommand extends CommandAPICommand {

  public ChannelForceJoinCommand(String commandName) {
    super(commandName);

    withArguments(new ChannelArgument("channel"));
    executesPlayer((player, args) -> {
      Channel channel = args.getUnchecked("channel");

      channel.join(player);
    });
  }
}
