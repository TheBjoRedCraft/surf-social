package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;

public class ChannelDeleteCommand extends CommandAPICommand {

  public ChannelDeleteCommand(String commandName) {
    super(commandName);

    executesPlayer((player, args) -> {
      Channel channel = Channel.getChannel(player);

      if(channel == null) {
        return;
      }

      if(!channel.isOwner(player)) {
        return;
      }

      channel.delete();

      SurfChat.message(player, new MessageBuilder().primary("Du hast den Nachrichtenkanal ").info(channel.getName()).success(" gelöscht."));
    });
  }
}
