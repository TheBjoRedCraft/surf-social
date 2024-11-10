package dev.slne.surf.friends.command.subcommand;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.slne.surf.friends.FriendManager;
import dev.slne.surf.friends.core.FriendCore;
import dev.slne.surf.friends.core.util.PluginColor;

import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;

public class FriendJumpCommand extends CommandAPICommand {

  public FriendJumpCommand(String commandName) {
    super(commandName);

    withArguments(new OfflinePlayerArgument("target"));

    executesPlayer((player, args)-> {
      OfflinePlayer target = args.getUnchecked("target");

      if (target == null) {
        throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.");
      }

      if(!FriendManager.instance().areFriends(player.getUniqueId(), target.getUniqueId())){
        throw CommandAPI.failWithString("Du bist nicht mit " + target.getName() + " befreundet.");
      }

      player.sendMessage(FriendCore.prefix().append(Component.text("Du wirst mit dem Server von " + target.getName() + " verbunden.")));

      //TODO: Cloud implementation: send player

      player.sendMessage(FriendCore.prefix().append(Component.text("Du wurdest erfolgreich mit dem Server von " + target.getName() + " verbunden.").color(PluginColor.LIGHT_GREEN)));

      //TODO: Cloud implementation
    });
  }
}
