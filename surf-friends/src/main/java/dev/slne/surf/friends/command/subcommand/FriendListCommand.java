package dev.slne.surf.friends.command.subcommand;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.friends.FriendManager;
import dev.slne.surf.friends.SurfFriendsPlugin;

import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Bukkit;

import java.util.UUID;

public class FriendListCommand extends CommandAPICommand {
  public FriendListCommand(String commandName) {
    super(commandName);

    executesPlayer((player, args) -> {
      FriendManager.instance().getFriends(player.getUniqueId()).thenAccept(friends ->  {
        StringBuilder message = new StringBuilder("Deine Freunde: <gray>(<yellow>" + friends.size() + "<gray>) <white>");
        int current = 0;

        for (UUID uuid : friends) {
          current ++;

          if(current == friends.size()) {
            message.append("<white>").append(Bukkit.getOfflinePlayer(uuid).getName());
          }else{
            message.append("<white>").append(Bukkit.getOfflinePlayer(uuid).getName()).append("<gray>, ");
          }
        }

        player.sendMessage(SurfFriendsPlugin.getPrefix().append(MiniMessage.miniMessage().deserialize(message.toString())));
      });
    });
  }
}
