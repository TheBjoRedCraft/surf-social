package dev.slne.surf.friends.command.subcommand;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.friends.FriendManager;
import dev.slne.surf.friends.SurfFriendsPlugin;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.UUID;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

public class FriendRequestListCommand extends CommandAPICommand {
  public FriendRequestListCommand(String commandName) {
    super(commandName);

    executesPlayer((player, args) -> {
      String message = "Du hast keine Freunde.";

      ObjectList<UUID> requests = FriendManager.instance().getFriendRequests(player.getUniqueId());
      StringBuilder builder = new StringBuilder("Du hast aktuell <yellow>" + requests.size() + "<white> Freundschaftsanfragen offen: ");
      int current = 0;

      for (UUID uuid : requests) {
        current ++;

        if(current == requests.size()) {
          builder.append("<white>").append(Bukkit.getOfflinePlayer(uuid).getName());
        }else{
          builder.append("<white>").append(Bukkit.getOfflinePlayer(uuid).getName()).append("<gray>, ");
        }
      }

      if(!requests.isEmpty()) {
        message = builder.toString();
      }

      player.sendMessage(SurfFriendsPlugin.getPrefix().append(MiniMessage.miniMessage().deserialize(message)));
    });
  }
}
