package dev.slne.surf.friends.listener;

import dev.slne.surf.friends.FriendManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();

    FriendManager.instance().saveFriendData(player.getUniqueId());
  }
}
