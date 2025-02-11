package dev.slne.surf.social.chat.command.channel;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;

import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.object.Channel;

import dev.slne.surf.social.chat.util.MessageBuilder;
import org.bukkit.OfflinePlayer;

public class ChannelInviteRevokeCommand extends CommandAPICommand {

  public ChannelInviteRevokeCommand(String commandName) {
    super(commandName);

    withArguments(new OfflinePlayerArgument("player"));
    executesPlayer((player, args) -> {
      Channel channel = Channel.getChannel(player);
      OfflinePlayer target = args.getUnchecked("player");

      if(channel == null) {
        return;
      }

      if(!channel.isModerator(player)) {
        return;
      }

      channel.revokeInvite(target);

      SurfChat.message(player, new MessageBuilder().primary("Du hast die Einladung für ").secondary(target.getName()).primary(" in den Nachrichtenkanal ").secondary(channel.getName()).success(" zurückgezogen."));
      SurfChat.message(target, new MessageBuilder().primary("Deine Einladung in den Nachrichtenkanal ").secondary(channel.getName()).primary(" wurde zurückgezogen."));
    });
  }
}
