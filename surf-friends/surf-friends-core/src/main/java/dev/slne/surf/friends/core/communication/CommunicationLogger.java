package dev.slne.surf.friends.core.communication;

import dev.slne.surf.friends.core.util.PluginColor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommunicationLogger {
  public static void logAction(CommunicationLogType type, String message){
    for (Player player : Bukkit.getOnlinePlayers()) {
      if(player.hasPermission("surf-social.friends.communication.log")) {
        switch (type) {
          case SEND_TO_PROXY, SEND_TO_BACKEND, RECEIVED_FROM_BACKEND, RECEIVED_FROM_PROXY ->
              player.sendMessage(prefix(type).append(Component.text(message)));
        }
      }
    }
  }

  private static Component prefix(CommunicationLogType type){
    Component prefix = Component.text("[").color(PluginColor.LIGHT_GRAY);

    switch (type){
      case SEND_TO_PROXY -> prefix = prefix.append(Component.text("BACKEND -> PROXY"));
      case SEND_TO_BACKEND -> prefix = prefix.append(Component.text("PROXY -> BACKEND"));
      case RECEIVED_FROM_PROXY -> prefix = prefix.append(Component.text("BACKEND #- PROXY"));
      case RECEIVED_FROM_BACKEND -> prefix = prefix.append(Component.text("PROXY #- BACKEND"));
    }

    prefix = prefix.append(Component.text("] ").color(PluginColor.LIGHT_GRAY));


    return prefix;
  }
}
