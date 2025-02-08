package dev.slne.surf.social.chat.listener;

import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.object.Message;
import dev.slne.surf.social.chat.provider.ConfigProvider;
import dev.slne.surf.social.chat.service.ChatFilterService;
import dev.slne.surf.social.chat.service.ChatHistoryService;

import io.papermc.paper.event.player.AsyncChatEvent;

import me.clip.placeholderapi.PlaceholderAPI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.Bukkit;
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

    if(ChatFilterService.getInstance().containsBlocked(event.message())) {
      event.setCancelled(true);
      player.sendMessage(SurfChat.getPrefix().append(Component.text("Bitte achte auf deine Wortwahl!", NamedTextColor.RED)));
      return;
    }

    Component message = MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, ConfigProvider.getInstance().getPublicMessageFormat())).replaceText(TextReplacementConfig.builder()
        .matchLiteral("%message%")
        .replacement(event.message())
        .build());
    Bukkit.getOnlinePlayers().forEach(online -> ChatHistoryService.getInstance().insertNewMessage(online.getUniqueId(), Message.builder()
        .receiver(online.getName())
        .sender(player.getName())
        .message(message)
        .build()));

    event.message(message);
  }
}
