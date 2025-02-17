package dev.slne.surf.social.chat.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.object.ChatUser;
import dev.slne.surf.social.chat.util.MessageBuilder;

public class TogglePmCommand extends CommandAPICommand {

  public TogglePmCommand(String commandName) {
    super(commandName);

    withPermission("surf.chat.command.toggle");
    executesPlayer((player, args) -> {
      ChatUser user = ChatUser.getUser(player.getUniqueId());

      if(user.isToggledPM()) {
        user.setToggledPM(false);
        SurfChat.send(player, new MessageBuilder().primary("Du hast privat Nachrichten ").success("aktiviert."));
      } else {
        user.setToggledPM(true);
        SurfChat.send(player, new MessageBuilder().primary("Du hast privat Nachrichten ").error("deaktiviert."));
      }
    });
  }
}
