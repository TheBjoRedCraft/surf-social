package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.command.argument.ChannelInviteArgument;
import dev.slne.surf.social.chat.object.Channel;

public class ChannelAcceptInviteCommand extends CommandAPICommand {

  public ChannelAcceptInviteCommand(String commandName) {
    super(commandName);

    withArguments(new ChannelInviteArgument("channel"));
    executesPlayer((player, args) -> {
      Channel channel = args.getUnchecked("channel");

      channel.acceptInvite(player);
    });
  }
}
