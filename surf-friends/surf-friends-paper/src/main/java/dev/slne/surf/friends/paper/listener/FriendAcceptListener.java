package dev.slne.surf.friends.paper.listener;

import dev.slne.surf.friends.api.event.FriendRequestAcceptEvent;
import dev.slne.surf.friends.paper.FriendsPaperPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FriendAcceptListener implements Listener {
  @EventHandler
  public void onAccept(FriendRequestAcceptEvent event){
    Player player = Bukkit.getPlayer(event.getPlayer());
    Player target = Bukkit.getPlayer(event.getTarget());

    String targetName = Bukkit.getOfflinePlayer(event.getTarget()).getName();

    player.sendMessage(FriendsPaperPlugin.prefix().append(Component.text(String.format("Du bist nun mit %s befreundet.", targetName))));

    if(target != null){
      target.sendMessage(FriendsPaperPlugin.prefix().append(Component.text(String.format("Du bist nun mit %s befreundet.", player.getName()))));
    }
  }
}
