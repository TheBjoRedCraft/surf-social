package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.command.argument.ChannelModerateArgument;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.util.MessageBuilder;
import org.bukkit.OfflinePlayer;

public class ChannelUnBanCommand extends CommandAPICommand {

  public ChannelUnBanCommand(String commandName) {
    super(commandName);

    withArguments(new OfflinePlayerArgument("player"));
    executesPlayer((player, args) -> {
      OfflinePlayer target = args.getUnchecked("player");
      Channel channel = Channel.getChannel(player);

      if(channel == null) {
        return;
      }

      if(!channel.isModerator(player) && !channel.isOwner(player)) {
        return;
      }

      channel.unban(target);

      SurfChat.message(player, new MessageBuilder().primary("Du hast ").secondary(target.getName()).primary(" im Nachrichtenkanal ").secondary(channel.getName()).success(" entbannt."));
      SurfChat.message(target, new MessageBuilder().primary("Du wurdest im Nachrichtenkanal ").secondary(channel.getName()).success(" entbannt."));
    });
  }
}
