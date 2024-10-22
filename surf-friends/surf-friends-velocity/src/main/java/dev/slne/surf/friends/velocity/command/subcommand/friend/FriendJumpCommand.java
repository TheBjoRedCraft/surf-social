package dev.slne.surf.friends.velocity.command.subcommand.friend;

import com.velocitypowered.api.proxy.Player;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.friends.core.FriendCore;
import dev.slne.surf.friends.core.util.PluginColor;
import dev.slne.surf.friends.velocity.command.argument.PlayerArgument;

import net.kyori.adventure.text.Component;

public class FriendJumpCommand extends CommandAPICommand {

  public FriendJumpCommand(String commandName) {
    super(commandName);

    withArguments(PlayerArgument.player("target"));

    executesPlayer((player, args)-> {
      Player target = PlayerArgument.getPlayer("target", args);

      if (target == null) {
        throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.");
      }

      player.sendMessage(FriendCore.prefix().append(Component.text("Du wirst mit dem Server von " + target.getUsername() + " verbunden.")));

      if(target.getCurrentServer().isEmpty()){
        player.sendMessage(FriendCore.prefix().append(Component.text("Beim verbinden ist ein Fehler aufgetreten.").color(PluginColor.RED)));
        return;
      }

      player.createConnectionRequest(target.getCurrentServer().get().getServer());
      player.sendMessage(FriendCore.prefix().append(Component.text("Du wurdest erfolgreich mit dem Server von " + target.getUsername() + " verbunden.").color(PluginColor.LIGHT_GREEN)));
    });
  }
}
