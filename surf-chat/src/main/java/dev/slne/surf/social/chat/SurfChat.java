package dev.slne.surf.social.chat;

import dev.slne.surf.social.chat.command.PrivateMessageCommand;
import dev.slne.surf.social.chat.command.SurfChatCommand;
import dev.slne.surf.social.chat.command.SurfChatDeleteCommand;
import dev.slne.surf.social.chat.command.channel.ChannelCommand;
import dev.slne.surf.social.chat.listener.PlayerAsyncChatListener;
import dev.slne.surf.social.chat.listener.PlayerQuitListener;
import dev.slne.surf.social.chat.service.ChatFilterService;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SurfChat extends JavaPlugin {
  @Override
  public void onLoad() {
    ChatFilterService.getInstance().loadBlockedWords();
  }

  @Override
  public void onEnable() {
    new PrivateMessageCommand("msg").register();
    new ChannelCommand("channel").register();
    new SurfChatCommand("surfchat").register();

    Bukkit.getPluginManager().registerEvents(new PlayerAsyncChatListener(), this);
    Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
  }

  public static SurfChat getInstance() {
    return getPlugin(SurfChat.class);
  }
  public static Component getPrefix() {
    return MiniMessage.miniMessage().deserialize("<gray>>> <gold>SC <gray>| <white>");
  }
}
