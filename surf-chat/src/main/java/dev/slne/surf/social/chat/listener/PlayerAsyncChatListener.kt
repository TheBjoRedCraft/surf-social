package dev.slne.surf.social.chat.listener;
import dev.slne.surf.social.chat.SurfChat;
import dev.slne.surf.social.chat.external.BasicPunishApi;
import dev.slne.surf.social.chat.object.Channel;
import dev.slne.surf.social.chat.object.Message;
import dev.slne.surf.social.chat.service.ChatFilterService;
import dev.slne.surf.social.chat.service.ChatHistoryService;

import dev.slne.surf.social.chat.util.Colors;
import dev.slne.surf.social.chat.util.MessageBuilder;
import dev.slne.surf.social.chat.util.PluginColor;
import io.papermc.paper.event.player.AsyncChatEvent;

import java.security.SecureRandom;
import lombok.Getter;
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

@Getter
public class PlayerAsyncChatListener implements Listener {
  private final SecureRandom random = new SecureRandom();
  private final String deletePerms = "surf.chat.delete";
  private final String teleportPerms = "surf.chat.teleport";

  @EventHandler
  public void onChat(AsyncChatEvent event) {
    Player player = event.getPlayer();
    String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());

    if(event.isCancelled()) {
      return;
    }

    if(ChatFilterService.getInstance().containsLink(event.message())) {
      event.setCancelled(true);
      SurfChat.send(player, new MessageBuilder().error("Bitte sende keine Links!"));
      return;
    }

    if(ChatFilterService.getInstance().containsBlocked(event.message())) {
      event.setCancelled(true);
      SurfChat.send(player, new MessageBuilder().error("Bitte achte auf deine Wortwahl!"));
      return;
    }

    if(ChatFilterService.getInstance().isSpamming(event.getPlayer().getUniqueId())) {
      event.setCancelled(true);
      SurfChat.send(player, new MessageBuilder().error("Mal ganz ruhig hier, spam bitte nicht!"));
      return;
    }

    if(!ChatFilterService.getInstance().isValidInput(plainMessage)) {
      event.setCancelled(true);
      SurfChat.send(player, new MessageBuilder().error("Bitte verwende keine unerlaubten Zeichen!"));
      return;
    }

    if(BasicPunishApi.isMuted(player)) {
      SurfChat.send(player, new MessageBuilder().error("Du bist gemuted und kannst nicht chatten."));
      event.setCancelled(true);
      return;
    }

    event.setCancelled(true);

    Channel channel = Channel.getChannel(player);
    int messageID = random.nextInt(1000000);
    boolean found = false;

    if (channel != null) {
      if (plainMessage.startsWith("@all")) {
        plainMessage = plainMessage.replaceFirst("@all", "").trim();
        found = true;
      } else if (plainMessage.startsWith("@a")) {
        plainMessage = plainMessage.replaceFirst("@a", "").trim();
        found = true;
      }

      if(!found) {
        for (Player onlinePlayer : channel.getOnlinePlayers()) {

          SurfChat.send(onlinePlayer, new MessageBuilder().component(this.getDeleteComponent(onlinePlayer, messageID)).component(this.getTeleportComponent(onlinePlayer, player.getName())).miniMessage(PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix% %player_name%")).darkSpacer(" >> ").component(this.getChannelComponent(channel)).miniMessage("<white>" + plainMessage));
        }
      }

      return;
    }

    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      SurfChat.send(onlinePlayer, new MessageBuilder().component(this.getDeleteComponent(onlinePlayer, messageID)).component(this.getTeleportComponent(onlinePlayer, player.getName())).miniMessage(PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix% %player_name%")).darkSpacer(" >> ").miniMessage("<white>" + plainMessage));
    }
  }

  private Component getDeleteComponent(Player player, int id) {
    return player.hasPermission(this.deletePerms) ? Component.text("[", Colors.DARK_SPACER).append(Component.text("DEL", Colors.VARIABLE_KEY)).append(Component.text("] ", Colors.DARK_SPACER))
        .clickEvent(ClickEvent.runCommand("/surfchat delete " + id))
        .hoverEvent(Component.text("Nachricht löschen", PluginColor.RED)) : Component.empty();
  }

  private Component getChannelComponent(Channel channel) {
    return new MessageBuilder().darkSpacer("[").variableKey(channel.getName()).darkSpacer("] ").build();
  }

  private Component getTeleportComponent(Player player, String name) {
    return player.hasPermission(this.teleportPerms) ? Component.text("[", Colors.DARK_SPACER).append(Component.text("TP", Colors.VARIABLE_KEY)).append(Component.text("] ", Colors.DARK_SPACER))
        .clickEvent(ClickEvent.runCommand("/tp " + name))
        .hoverEvent(Component.text("Zum Spieler teleportieren", PluginColor.BLUE_MID)) : Component.empty();
  }
}
