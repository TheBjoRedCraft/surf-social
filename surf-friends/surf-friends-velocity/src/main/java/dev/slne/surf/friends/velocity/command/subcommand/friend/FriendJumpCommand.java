package dev.slne.surf.friends.velocity.command.subcommand.friend;

import com.velocitypowered.api.proxy.Player;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;

import dev.slne.surf.friends.core.FriendCore;
import dev.slne.surf.friends.core.util.PluginColor;
import dev.slne.surf.friends.velocity.VelocityInstance;

import java.util.Optional;

import net.kyori.adventure.text.Component;

public class FriendJumpCommand extends CommandAPICommand {

  public FriendJumpCommand(String commandName) {
    super(commandName);

    withArguments(new StringArgument("player"));

    executesPlayer((player, info)-> {
      Optional<Player> target = VelocityInstance.instance().proxy().getPlayer((String) info.getUnchecked("player"));

      if(target.isEmpty()) {
        player.sendMessage(FriendCore.prefix().append(Component.text("Der Spieler wurde nicht gefunden.").color(PluginColor.RED)));
        return;
      }

      player.sendMessage(FriendCore.prefix().append(Component.text("Du wirst mit dem Server von " + target.get().getUsername() + " verbunden.")));

      if(!target.get().getCurrentServer().isEmpty()){
        player.sendMessage(FriendCore.prefix().append(Component.text("Beim verbinden ist ein Fehler aufgetreten.").color(PluginColor.RED)));
      }

      player.createConnectionRequest(target.get().getCurrentServer().get().getServer());
      player.sendMessage(FriendCore.prefix().append(Component.text("Du wurdest erfolgreich mit dem Server von " + target.get().getUsername() + " verbunden.").color(PluginColor.LIGHT_GREEN)));
    });
  }
}
