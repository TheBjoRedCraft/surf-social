package dev.slne.surf.social.chat.listener;

import dev.slne.surf.social.chat.provider.ConfigProvider;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerAsyncChatListener implements Listener {
  @EventHandler
  public void onChat(AsyncChatEvent event) {
    Player player = event.getPlayer();

    if(event.isCancelled()) {
      return;
    }

    event.message(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, ConfigProvider.getInstance().getPublicMessageFormat()).replace("%player%", player.getName())));
  }
}
