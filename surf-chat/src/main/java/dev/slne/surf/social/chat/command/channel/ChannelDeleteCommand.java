package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.social.chat.object.Channel;

public class ChannelDeleteCommand extends CommandAPICommand {

  public ChannelDeleteCommand(String commandName) {
    super(commandName);


    withRequirement((sender) -> {
      Channel channel = Channel.getChannel(sender);

      if(channel == null) {
        return false;
      }

      return channel.isOwner(sender);
    });
    executesPlayer((player, args) -> {
      Channel channel = Channel.getChannel(player);

      if(channel == null) {
        return;
      }

      if(!channel.isOwner(player)) {
        return;
      }

      channel.delete();
    });
  }
}
