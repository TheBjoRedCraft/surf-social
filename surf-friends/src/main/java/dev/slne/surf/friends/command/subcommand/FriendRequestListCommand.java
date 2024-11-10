package dev.slne.surf.friends.command.subcommand;

import dev.jorel.commandapi.CommandAPICommand;
import dev.slne.surf.friends.FriendManager;
import dev.slne.surf.friends.SurfFriendsPlugin;
import java.util.UUID;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

public class FriendRequestListCommand extends CommandAPICommand {
  public FriendRequestListCommand(String commandName) {
    super(commandName);

    executesPlayer((player, args) -> {
      FriendManager.instance().getFriendRequests(player.getUniqueId()).thenAccept(friendRequests ->  {
        StringBuilder message = new StringBuilder("Du hast aktuell <yellow>" + friendRequests.size() + "<white> Freundschaftsanfragen offen: ");
        int current = 0;

        for (UUID uuid : friendRequests) {
          current ++;

          if(current == friendRequests.size()) {
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
