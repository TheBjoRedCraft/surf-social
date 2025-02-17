package dev.slne.surf.social.chat.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.external.BasicPunishApi;
import dev.slne.surf.social.chat.object.ChatUser;
import dev.slne.surf.social.chat.util.MessageBuilder;
import org.bukkit.entity.Player;

public class PrivateMessageCommand extends CommandAPICommand {
  public PrivateMessageCommand(String commandName) {
    super(commandName);

    withArguments(new PlayerArgument("player"));
    withArguments(new GreedyStringArgument("message"));
    withAliases("tell", "w", "pm", "dm");
    withPermission("surf.chat.command.private-message");
    executesPlayer((player, args) -> {
      Player target = args.getUnchecked("player");
      String message = args.getUnchecked("message");

      ChatUser targetUser = ChatUser.getUser(target.getUniqueId());


      if(BasicPunishApi.isMuted(player)) {
        SurfChat.send(player, new MessageBuilder().error("Du bist gemuted und kannst nicht schreiben."));
        return;
      }

      if(targetUser.isToggledPM()) {
        SurfChat.send(player, new MessageBuilder().error("Der Spieler hat private Nachrichten deaktiviert."));
        return;
      }


      SurfChat.send(target, new MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ").variableValue(player.getName()).darkSpacer(" ->").variableValue(" Dich ").info(message));
      SurfChat.send(player, new MessageBuilder().darkSpacer(">>").error(" PM ").darkSpacer("| ").variableValue("Du").darkSpacer(" -> ").variableValue(player.getName() + " ").info(message));
    });
  }
}
