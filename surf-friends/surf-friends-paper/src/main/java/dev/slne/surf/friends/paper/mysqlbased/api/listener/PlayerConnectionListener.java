package dev.slne.surf.friends.paper.mysqlbased.api.listener;

import dev.slne.surf.friends.paper.mysqlbased.api.FriendApiFallback;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
  @EventHandler
  public void onJoin(PlayerJoinEvent event){
    FriendApiFallback.getInstance().handleJoin(event);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event){
    FriendApiFallback.getInstance().handleQuit(event);
  }
}
