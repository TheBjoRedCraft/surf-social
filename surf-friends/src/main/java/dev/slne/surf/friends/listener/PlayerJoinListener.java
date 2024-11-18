package dev.slne.surf.friends.listener;

import dev.slne.surf.friends.FriendManager;
import dev.slne.surf.friends.SurfFriendsPlugin;
import dev.slne.surf.friends.util.PluginColor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    FriendManager.loadFriendDataAsync(player.getUniqueId()).thenAccept(friendData -> {
      FriendManager.instance().cache().put(player.getUniqueId(), friendData);

      player.sendMessage(SurfFriendsPlugin.getPrefix().append(Component.text("Willkommen zurück!")));
      player.sendMessage(SurfFriendsPlugin.getPrefix().append(Component.text("Du hast noch ")).append(Component.text(friendData.getFriendRequests().size(), PluginColor.GOLD)).append(Component.text(" Freundschaftsanfragen offen.")));
    });
  }
}
