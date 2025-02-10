package dev.slne.surf.social.chat.listener;

import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.external.BasicPunishApi;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.object.Message;
import dev.slne.surf.social.chat.provider.ConfigProvider;
import dev.slne.surf.social.chat.service.ChatFilterService;
import dev.slne.surf.social.chat.service.ChatHistoryService;

import dev.slne.surf.social.chat.util.PluginColor;
import io.papermc.paper.event.player.AsyncChatEvent;

import java.security.SecureRandom;
import me.clip.placeholderapi.PlaceholderAPI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerAsyncChatListener implements Listener {
  private final SecureRandom random = new SecureRandom();
  private final String deletePerms = "surf.chat.delete";
  private final String teleportPerms = "surf.chat.teleport";

  @EventHandler
  public void onChat(AsyncChatEvent event) {
    Player player = event.getPlayer();

    if(event.isCancelled()) {
      return;
    }

    if(ChatFilterService.getInstance().containsLink(event.message())) {
      event.setCancelled(true);
      player.sendMessage(SurfChat.getPrefix().append(Component.text("Bitte sende keine Links!", NamedTextColor.RED)));
      return;
    }

    if(ChatFilterService.getInstance().containsBlocked(event.message())) {
      event.setCancelled(true);
      player.sendMessage(SurfChat.getPrefix().append(Component.text("Bitte achte auf deine Wortwahl!", NamedTextColor.RED)));
      return;
    }

    if(BasicPunishApi.isMuted(player)) {
      event.setCancelled(true);
      return;
    }

    String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
    Channel channel = Channel.getChannel(player);
    int messageID = this.getRandomID();

    if(channel != null && !plainMessage.startsWith("@all") && !plainMessage.startsWith("@a")) {
      for (Player onlinePlayer : channel.getOnlinePlayers()) {
        Component message = MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, ConfigProvider.getInstance().getPublicMessageFormat()))
            .replaceText(TextReplacementConfig.builder()
                .matchLiteral("%message%")
                .replacement(event.message())
                .build())
            .replaceText(TextReplacementConfig.builder()
                .matchLiteral("%channel%")
                .replacement(" " + channel.getName() + " ")
                .build())
            .replaceText(TextReplacementConfig.builder()
                .matchLiteral("%delete%")
                .replacement(player.hasPermission(this.deletePerms) ? this.getDeleteComponent(messageID) : Component.empty())
                .build())
            .replaceText(TextReplacementConfig.builder()
                .matchLiteral("%teleport%")
                .replacement(player.hasPermission(this.teleportPerms) ? this.getTeleportComponent(player.getName()) : Component.empty())
                .build())
            ;

        onlinePlayer.sendMessage(message);
        ChatHistoryService.getInstance().insertNewMessage(onlinePlayer.getUniqueId(), Message.builder()
            .receiver(onlinePlayer.getName())
            .sender(player.getName())
            .message(message)
            .build(), messageID);
      }

      event.setCancelled(true);
      return;
    }

    Component message = MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, ConfigProvider.getInstance().getPublicMessageFormat()))
        .replaceText(TextReplacementConfig.builder()
            .matchLiteral("%message%")
            .replacement(event.message())
            .build())
        .replaceText(TextReplacementConfig.builder()
            .matchLiteral("%channel%")
            .replacement(channel == null ? "" : " " + channel.getName() + " ")
            .build())
        .replaceText(TextReplacementConfig.builder()
            .matchLiteral("%delete%")
            .replacement(player.hasPermission(this.deletePerms) ? this.getDeleteComponent(messageID) : Component.empty())
            .build())
        .replaceText(TextReplacementConfig.builder()
            .matchLiteral("%teleport%")
            .replacement(player.hasPermission(this.teleportPerms) ? this.getTeleportComponent(player.getName()) : Component.empty())
            .build())
        ;

    event.message(message);

    Bukkit.getOnlinePlayers().forEach(online -> ChatHistoryService.getInstance().insertNewMessage(online.getUniqueId(), Message.builder()
        .receiver(online.getName())
        .sender(player.getName())
        .message(message)
        .build(), messageID));
  }

  private Component getDeleteComponent(int id) {
    return Component.text(" [", PluginColor.LIGHT_GRAY).append(Component.text("DEL", PluginColor.RED)).append(Component.text("] ", PluginColor.LIGHT_GRAY))
        .clickEvent(ClickEvent.runCommand("/surfchat delete " + id))
        .hoverEvent(Component.text("Nachricht löschen", PluginColor.RED));
  }

  private Component getTeleportComponent(String name) {
    return Component.text(" [", PluginColor.LIGHT_GRAY).append(Component.text("TP", PluginColor.BLUE_MID)).append(Component.text("] ", PluginColor.LIGHT_GRAY))
        .clickEvent(ClickEvent.runCommand("/tp " + name))
        .hoverEvent(Component.text("Zum Spieler teleportieren", PluginColor.BLUE_MID))
        ;
  }

  private int getRandomID() {
    return this.random.nextInt(1000000);
  }
}
