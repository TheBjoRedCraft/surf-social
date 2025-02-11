package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;
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

      SurfChat.message(player, new MessageBuilder().primary("Du hast den Spieler ").secondary(target.getName()).info(" degradiert."));
      SurfChat.message(target, new MessageBuilder().primary("Du wurdest ").info("degradiert").primary(" und bist nun ein normaler Spieler im Nachrichtenkanal ").secondary(channel.getName()).primary("."));
    });
  }
}
