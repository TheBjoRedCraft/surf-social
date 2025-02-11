package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;
import org.bukkit.OfflinePlayer;

public class ChannelPromoteCommand extends CommandAPICommand {

  public ChannelPromoteCommand(String commandName) {
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

      channel.promote(target);

      SurfChat.message(player, new MessageBuilder().primary("Du hast den Spieler ").info(target.getName()).success(" befördert."));
      SurfChat.message(target, new MessageBuilder().primary("Du wurdest ").success("befördert").primary(" und bist nun ein Moderator im Nachrichtenkanal ").info(channel.getName()).primary("."));
    });
  }
}
