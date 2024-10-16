package dev.slne.surf.friends.paper.listener;

import dev.slne.surf.friends.core.FriendCore;
import dev.slne.surf.friends.paper.communication.CommunicationHandler;

import dev.slne.surf.friends.paper.communication.RequestType;
import net.kyori.adventure.text.Component;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event){
    Player player = event.getPlayer();

    CommunicationHandler.instance().sendRequest(RequestType.REQUESTS, player, null);

    if(!CommunicationHandler.instance().cachedRequests().isEmpty()){
      player.sendMessage(FriendCore.prefix().append(Component.text(String.format("Du hast noch %s Freundschaftsanfragen offen.", CommunicationHandler.instance().cachedRequests().size()))));
    }
  }
}