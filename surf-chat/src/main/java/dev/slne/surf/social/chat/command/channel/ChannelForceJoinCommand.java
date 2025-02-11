package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.command.argument.ChannelArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;

public class ChannelForceJoinCommand extends CommandAPICommand {

  public ChannelForceJoinCommand(String commandName) {
    super(commandName);

    withPermission("surf.chat.command.channel.forcejoin");
    withArguments(new ChannelArgument("channel"));
    executesPlayer((player, args) -> {
      Channel channel = args.getUnchecked("channel");

      channel.join(player);

      SurfChat.message(player, new MessageBuilder().primary("Du bist dem Nachrichtenkanal ").secondary(channel.getName()).success(" beigetreten."));
    });
  }
}
