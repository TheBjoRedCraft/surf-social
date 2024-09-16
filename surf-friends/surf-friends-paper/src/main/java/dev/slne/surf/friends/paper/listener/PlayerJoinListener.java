package dev.slne.surf.friends.paper.listener;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.core.util.FriendLogger;
import dev.slne.surf.friends.paper.FriendsPaperPlugin;
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
    FriendLogger logger = FriendsPaperPlugin.logger();
    FriendApi api = FriendsPaperPlugin.instance().api();

    try {
      if(!api.getFriendRequests(player.getUniqueId()).get().isEmpty()){
        player.sendMessage(FriendsPaperPlugin.prefix().append(Component.text(String.format("Du hast noch %s Freundschaftsanfragen offen.",
            api.getFriendRequests(player.getUniqueId()).get().size()))));
      }
    } catch (InterruptedException | ExecutionException e) {
      logger.error(e.getMessage());
    }
  }
}
