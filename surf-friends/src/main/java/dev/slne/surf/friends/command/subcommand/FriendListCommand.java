package dev.slne.surf.friends.command.subcommand;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.friends.FriendManager;
import dev.slne.surf.friends.SurfFriendsPlugin;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Bukkit;

import java.util.UUID;

public class FriendListCommand extends CommandAPICommand {
  public FriendListCommand(String commandName) {
    super(commandName);

    executesPlayer((player, args) -> {
      String message = "Du hast keine Freunde.";

      ObjectList<UUID> friends = FriendManager.instance().getFriends(player.getUniqueId());
      StringBuilder builder = new StringBuilder("Momentan hast du <yellow>" + friends.size() + "<white> Freunde: ");
      int current = 0;

      for (UUID uuid : friends) {
        current ++;

        if(current == friends.size()) {
          builder.append("<white>").append(Bukkit.getOfflinePlayer(uuid).getName());
        }else{
          builder.append("<white>").append(Bukkit.getOfflinePlayer(uuid).getName()).append("<gray>, ");
        }
      }

      if(!friends.isEmpty()) {
        message = builder.toString();
      }

      player.sendMessage(SurfFriendsPlugin.getPrefix().append(MiniMessage.miniMessage().deserialize(message)));
    });
  }
}
