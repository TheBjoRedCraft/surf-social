package dev.slne.surf.social.chat.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;

import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.object.ChatUser;
import dev.slne.surf.social.chat.util.MessageBuilder;
import org.bukkit.OfflinePlayer;

public class IgnoreCommand extends CommandAPICommand {

  public IgnoreCommand(String commandName) {
    super(commandName);

    withPermission("surf.chat.command.ignore");
    withArguments(new OfflinePlayerArgument("player"));
    executesPlayer((player, args)-> {
      OfflinePlayer target = args.getUnchecked("player");
      ChatUser user = ChatUser.getUser(player.getUniqueId());
      ChatUser targetUser = ChatUser.getUser(target.getUniqueId());

      if(user.isIgnoring(targetUser.getUuid())) {
        user.getIgnoreList().remove(targetUser.getUuid());
        SurfChat.send(player, new MessageBuilder().primary("Du hast ").info(target.getName()).success(" entstummt."));
      } else {
        user.getIgnoreList().add(targetUser.getUuid());
        SurfChat.send(player, new MessageBuilder().primary("Du hast ").info(target.getName()).error(" stumm geschaltet."));
      }
    });
  }
}
