package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;

import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.object.Channel;

import dev.slne.surf.social.chat.util.MessageBuilder;
import org.bukkit.OfflinePlayer;

public class ChannelKickCommand extends CommandAPICommand {

  public ChannelKickCommand(String commandName) {
    super(commandName);

    withArguments(new OfflinePlayerArgument("player"));
    executesPlayer((player, args) -> {
      Channel channel = Channel.getChannel(player);
      OfflinePlayer target = args.getUnchecked("player");

      if(channel == null) {
        return;
      }

      if(!channel.isModerator(player) && !channel.isOwner(player)) {
        return;
      }

      channel.kick(target);

      SurfChat.message(player, new MessageBuilder().primary("Du hast ").info(target.getName()).primary(" aus dem Nachrichtenkanal ").info(channel.getName()).error(" geworfen."));
      SurfChat.message(target, new MessageBuilder().primary("Du wurdest aus dem Nachrichtenkanal ").info(channel.getName()).error(" geworfen."));
    });
  }
}
