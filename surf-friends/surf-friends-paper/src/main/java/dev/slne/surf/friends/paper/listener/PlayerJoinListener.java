package dev.slne.surf.friends.paper.listener;

import dev.slne.surf.friends.api.fallback.FriendApiFallbackInstance;
import dev.slne.surf.friends.core.FriendCore;
import dev.slne.surf.friends.core.util.FriendLogger;

import dev.slne.surf.friends.paper.PaperInstance;
import java.util.concurrent.ExecutionException;
import net.kyori.adventure.text.Component;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event){
    Player player = event.getPlayer();
    FriendLogger logger = PaperInstance.instance().logger();

    try {
      if(!FriendApiFallbackInstance.instance().friendApi().getFriendRequests(player.getUniqueId()).get().isEmpty()){
        player.sendMessage(FriendCore.prefix().append(Component.text(String.format("Du hast noch %s Freundschaftsanfragen offen.", FriendApiFallbackInstance.instance().friendApi().getFriendRequests(player.getUniqueId()).get().size()))));
      }
    } catch (InterruptedException | ExecutionException e) {
      logger.error(e.getMessage());
    }
  }
}