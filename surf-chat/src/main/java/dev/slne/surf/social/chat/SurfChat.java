package dev.slne.surf.social.chat;

import dev.jorel.commandapi.CommandAPI;
import dev.slne.surf.social.chat.command.PrivateMessageCommand;
import dev.slne.surf.social.chat.command.SurfChatCommand;
import dev.slne.surf.social.chat.command.channel.ChannelCommand;
import dev.slne.surf.social.chat.listener.PlayerAsyncChatListener;
import dev.slne.surf.social.chat.listener.PlayerQuitListener;
import dev.slne.surf.social.chat.object.Message;
import dev.slne.surf.social.chat.service.ChatFilterService;

import dev.slne.surf.social.chat.service.ChatHistoryService;
import dev.slne.surf.social.chat.service.DatabaseService;
import dev.slne.surf.social.chat.util.Colors;
import dev.slne.surf.social.chat.util.MessageBuilder;
import java.security.SecureRandom;
import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class SurfChat extends JavaPlugin {
  private static final SecureRandom random = new SecureRandom();

  @Override
  public void onEnable() {
    CommandAPI.unregister("msg");
    CommandAPI.unregister("tell");
    CommandAPI.unregister("w");

    new PrivateMessageCommand("msg").register();
    new ChannelCommand("channel").register();
    new SurfChatCommand("surfchat").register();

    this.saveDefaultConfig();

    ChatFilterService.getInstance().loadBlockedWords();
    DatabaseService.getInstance().connect();

    Bukkit.getPluginManager().registerEvents(new PlayerAsyncChatListener(), this);
    Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
  }

  @Override
  public void onDisable() {
    DatabaseService.getInstance().disconnect();
  }

  public static SurfChat getInstance() {
    return getPlugin(SurfChat.class);
  }

  public static void send(OfflinePlayer player, MessageBuilder text) {
    Component message = Colors.PREFIX.append(text.build());

    if(player.isOnline()) {
      player.getPlayer().sendMessage(message);

      ChatHistoryService.getInstance().insertNewMessage(player.getUniqueId(), new Message("Unknown", player.getName(), message), getRandomID());
    }
  }

  public static int getRandomID() {
    return random.nextInt(1000000);
  }
}
