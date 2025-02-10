package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.command.argument.ChannelArgument;
import dev.slne.surf.social.chat.object.Channel;

public class ChannelForceDeleteCommand extends CommandAPICommand {

  public ChannelForceDeleteCommand(String commandName) {
    super(commandName);

    withPermission("surf.chat.command.channel.forceDelete");
    withArguments(new ChannelArgument("channel"));
    executesPlayer((player, args) -> {
      Channel channel = args.getUnchecked("channel");

      channel.delete();
    });
  }
}
